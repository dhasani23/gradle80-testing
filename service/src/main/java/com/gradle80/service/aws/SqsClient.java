package com.gradle80.service.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AWS SQS service client.
 * 
 * This class provides functionality to send and receive messages from Amazon SQS queues.
 * It handles the communication with the AWS SQS service.
 */
@Service
public class SqsClient {

    private static final Logger logger = LoggerFactory.getLogger(SqsClient.class);
    
    private final AmazonSQS sqsClient;
    private final String queueUrl;

    /**
     * Constructs an SqsClient with the required dependencies.
     *
     * @param sqsClient the AmazonSQS client
     * @param queueUrl the SQS queue URL to interact with
     */
    @Autowired
    public SqsClient(AmazonSQS sqsClient, @Value("${aws.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        logger.info("Initialized SQS client for queue: {}", queueUrl);
    }

    /**
     * Sends a message to the configured SQS queue.
     *
     * @param message the message to send
     * @return the message ID from the send result
     */
    public String sendMessage(String message) {
        try {
            logger.debug("Sending message to queue: {}", queueUrl);
            
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message);
            
            SendMessageResult sendMessageResult = sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Message sent. Message ID: {}", sendMessageResult.getMessageId());
            
            return sendMessageResult.getMessageId();
        } catch (Exception e) {
            logger.error("Failed to send message to SQS queue: {}", queueUrl, e);
            // TODO: Implement retry mechanism for transient failures
            // FIXME: Consider using a dead-letter queue for failed messages
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }

    /**
     * Receives messages from the configured SQS queue.
     * By default, this method will attempt to receive up to 10 messages.
     *
     * @return a list of message bodies received from the queue
     */
    public List<String> receiveMessage() {
        try {
            logger.debug("Receiving messages from queue: {}", queueUrl);
            
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(5); // Using long polling
            
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
            
            logger.info("Received {} messages from queue", messages.size());
            
            return messages.stream()
                .map(Message::getBody)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to receive messages from SQS queue: {}", queueUrl, e);
            // TODO: Implement circuit breaker to prevent cascading failures
            return new ArrayList<>(); // Return empty list rather than failing
        }
    }

    /**
     * Deletes a message from the queue after it has been processed.
     *
     * @param receiptHandle the receipt handle of the message to delete
     */
    public void deleteMessage(String receiptHandle) {
        try {
            logger.debug("Deleting message with receipt handle: {}", receiptHandle);
            
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(receiptHandle);
            
            sqsClient.deleteMessage(deleteMessageRequest);
            
            logger.info("Message deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete message from SQS queue: {}", queueUrl, e);
            // FIXME: Message might be processed again if delete fails
            throw new RuntimeException("Failed to delete message from SQS", e);
        }
    }
    
    /**
     * Gets the queue URL used by this client.
     *
     * @return the configured SQS queue URL
     */
    public String getQueueUrl() {
        return this.queueUrl;
    }
}