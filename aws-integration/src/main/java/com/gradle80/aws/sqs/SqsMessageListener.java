package com.gradle80.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.gradle80.aws.sqs.model.SqsMessage;
import com.gradle80.aws.sqs.model.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SQS message listener implementation that handles receiving and processing
 * messages from Amazon SQS queues.
 * 
 * This service implements the SqsListener interface and manages background
 * threads that continuously poll SQS queues for new messages and delegate
 * them to the appropriate MessageProcessor.
 */
@Service
public class SqsMessageListener implements SqsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsMessageListener.class);
    private static final int DEFAULT_MAX_MESSAGES = 10;
    private static final int DEFAULT_VISIBILITY_TIMEOUT = 30;
    
    /**
     * Amazon SQS client for interacting with the SQS service.
     */
    private final AmazonSQS sqsClient;
    
    /**
     * Map of message processors registered for handling messages from specific queues.
     * The key is the queue URL, and the value is the processor responsible for that queue.
     */
    private final Map<String, MessageProcessor> messageProcessors;
    
    /**
     * Polling interval in milliseconds between consecutive SQS polling requests.
     */
    private long pollingIntervalMs;
    
    /**
     * Map to track the active listeners and their status.
     */
    private final Map<String, ExecutorService> activeListeners = new ConcurrentHashMap<>();
    
    /**
     * Map to track the running state of each listener.
     */
    private final Map<String, AtomicBoolean> listenerRunning = new ConcurrentHashMap<>();

    /**
     * Constructs a new SQS message listener with the specified SQS client and default polling interval.
     *
     * @param sqsClient the Amazon SQS client
     */
    @Autowired
    public SqsMessageListener(AmazonSQS sqsClient) {
        this(sqsClient, new HashMap<>(), 1000);
    }

    /**
     * Constructs a new SQS message listener with the specified SQS client,
     * message processors, and polling interval.
     *
     * @param sqsClient the Amazon SQS client
     * @param messageProcessors the map of queue URLs to message processors
     * @param pollingIntervalMs the polling interval in milliseconds
     */
    public SqsMessageListener(AmazonSQS sqsClient, Map<String, MessageProcessor> messageProcessors, long pollingIntervalMs) {
        this.sqsClient = sqsClient;
        this.messageProcessors = new ConcurrentHashMap<>(messageProcessors);
        this.pollingIntervalMs = pollingIntervalMs;
    }

    /**
     * Implementation of SqsListener interface method to register a message processor.
     */
    @Override
    public void registerMessageProcessor(String queueUrl, MessageProcessor processor) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            throw new IllegalArgumentException("Queue URL cannot be null or empty");
        }
        if (processor == null) {
            throw new IllegalArgumentException("Message processor cannot be null");
        }
        messageProcessors.put(queueUrl, processor);
        LOGGER.info("Registered message processor for queue: {}", queueUrl);
    }

    /**
     * Sets the polling interval for all listeners.
     *
     * @param pollingIntervalMs the polling interval in milliseconds
     * @throws IllegalArgumentException if the interval is less than or equal to zero
     */
    public void setPollingIntervalMs(long pollingIntervalMs) {
        if (pollingIntervalMs <= 0) {
            throw new IllegalArgumentException("Polling interval must be greater than zero");
        }
        this.pollingIntervalMs = pollingIntervalMs;
    }

    @Override
    public void startListening(String queueUrl) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            throw new IllegalArgumentException("Queue URL cannot be null or empty");
        }

        // Check if we have a processor registered for this queue
        if (!messageProcessors.containsKey(queueUrl)) {
            throw new IllegalStateException("No message processor registered for queue: " + queueUrl);
        }

        // Check if the listener is already running
        if (activeListeners.containsKey(queueUrl)) {
            throw new IllegalStateException("Listener already running for queue: " + queueUrl);
        }

        // Create and start the listener thread
        AtomicBoolean running = new AtomicBoolean(true);
        listenerRunning.put(queueUrl, running);

        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "sqs-listener-" + queueUrl);
            t.setDaemon(true);
            return t;
        });

        executor.submit(() -> {
            LOGGER.info("Starting SQS message listener for queue: {}", queueUrl);
            while (running.get()) {
                try {
                    receiveAndProcessMessages(queueUrl);
                    Thread.sleep(pollingIntervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("SQS listener thread interrupted for queue: {}", queueUrl);
                    break;
                } catch (Exception e) {
                    LOGGER.error("Error in SQS message listener for queue: " + queueUrl, e);
                    // Continue listening despite errors
                }
            }
            LOGGER.info("Stopped SQS message listener for queue: {}", queueUrl);
        });

        activeListeners.put(queueUrl, executor);
        LOGGER.info("Started SQS message listener for queue: {}", queueUrl);
    }

    @Override
    public void stopListening(String queueUrl) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            throw new IllegalArgumentException("Queue URL cannot be null or empty");
        }

        ExecutorService executor = activeListeners.get(queueUrl);
        AtomicBoolean running = listenerRunning.get(queueUrl);

        if (executor == null || running == null) {
            throw new IllegalStateException("No active listener found for queue: " + queueUrl);
        }

        // Signal the listener thread to stop
        running.set(false);
        
        // Shutdown the executor service (don't wait for tasks to complete)
        executor.shutdownNow();
        
        // Remove from active listeners
        activeListeners.remove(queueUrl);
        listenerRunning.remove(queueUrl);
        
        LOGGER.info("Stopped SQS message listener for queue: {}", queueUrl);
    }

    /**
     * Receives and processes messages from the specified SQS queue.
     * This method:
     * 1. Receives up to 10 messages from the queue
     * 2. Processes each message using the registered message processor
     * 3. Deletes successfully processed messages from the queue
     *
     * @param queueUrl the URL of the queue to receive messages from
     */
    public void receiveAndProcessMessages(String queueUrl) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            throw new IllegalArgumentException("Queue URL cannot be null or empty");
        }

        MessageProcessor processor = messageProcessors.get(queueUrl);
        if (processor == null) {
            throw new IllegalStateException("No message processor registered for queue: " + queueUrl);
        }

        // Create receive message request
        ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(DEFAULT_MAX_MESSAGES)
                .withVisibilityTimeout(DEFAULT_VISIBILITY_TIMEOUT)
                .withWaitTimeSeconds(5) // Long polling
                .withAttributeNames("All");

        try {
            // Receive messages from SQS
            ReceiveMessageResult result = sqsClient.receiveMessage(receiveRequest);
            
            if (result.getMessages() == null || result.getMessages().isEmpty()) {
                LOGGER.debug("No messages received from queue: {}", queueUrl);
                return;
            }
            
            LOGGER.debug("Received {} messages from queue: {}", result.getMessages().size(), queueUrl);
            
            // Process each message
            for (Message message : result.getMessages()) {
                try {
                    // Convert to our internal message format
                    SqsMessage sqsMessage = new SqsMessage(
                            message.getMessageId(),
                            message.getReceiptHandle(),
                            message.getBody(),
                            message.getAttributes(),
                            message.getMessageAttributes()
                    );
                    
                    // Process the message
                    processor.processMessage(sqsMessage);
                    
                    // Delete the message from the queue after successful processing
                    sqsClient.deleteMessage(new DeleteMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withReceiptHandle(message.getReceiptHandle()));
                    
                    LOGGER.debug("Successfully processed and deleted message: {} from queue: {}", 
                            message.getMessageId(), queueUrl);
                } catch (Exception e) {
                    LOGGER.error("Error processing message: " + message.getMessageId() + 
                            " from queue: " + queueUrl, e);
                    // Do not delete the message - it will become visible again after visibility timeout
                    // FIXME: Consider implementing a dead-letter queue mechanism for repeatedly failing messages
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error receiving messages from queue: " + queueUrl, e);
            // Let the exception bubble up to be handled by the caller
            throw e;
        }
    }

    /**
     * Checks if a listener is currently active for the specified queue.
     *
     * @param queueUrl the URL of the queue to check
     * @return true if a listener is active, false otherwise
     */
    public boolean isListening(String queueUrl) {
        return activeListeners.containsKey(queueUrl) && listenerRunning.get(queueUrl).get();
    }

    /**
     * Stops all active listeners when the service is being destroyed.
     */
    @PreDestroy
    public void stopAllListeners() {
        LOGGER.info("Stopping all active SQS listeners");
        
        // Create a copy of the keys to avoid concurrent modification
        for (String queueUrl : new HashMap<>(activeListeners).keySet()) {
            try {
                stopListening(queueUrl);
            } catch (Exception e) {
                LOGGER.error("Error stopping listener for queue: " + queueUrl, e);
            }
        }
        
        LOGGER.info("All SQS listeners stopped");
    }

    // TODO: Add support for batched message processing
    // TODO: Add support for custom visibility timeout per queue
    // TODO: Add health check methods to verify connectivity to SQS
}