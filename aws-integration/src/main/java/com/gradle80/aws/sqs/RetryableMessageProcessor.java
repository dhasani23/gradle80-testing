package com.gradle80.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.gradle80.aws.sqs.model.SqsMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of MessageProcessor that supports retry functionality and
 * sends failed messages to a Dead Letter Queue (DLQ) after a specified number of retries.
 *
 * This processor adds retry metadata to messages and tracks retry attempts.
 * Once the maximum retry count is reached, the message is forwarded to a
 * configured Dead Letter Queue with error details.
 */
public class RetryableMessageProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RetryableMessageProcessor.class);
    
    private static final String RETRY_COUNT_ATTRIBUTE = "RetryCount";
    private static final String ERROR_MESSAGE_ATTRIBUTE = "ErrorMessage";
    private static final String ERROR_TYPE_ATTRIBUTE = "ErrorType";
    private static final String ORIGINAL_TIMESTAMP_ATTRIBUTE = "OriginalTimestamp";
    
    /**
     * The SQS client used for sending messages to the Dead Letter Queue.
     */
    private final AmazonSQS sqsClient;
    
    /**
     * The URL of the Dead Letter Queue where failed messages will be sent.
     */
    private final String deadLetterQueueUrl;
    
    /**
     * The maximum number of retry attempts before sending to the Dead Letter Queue.
     */
    private final int maxRetries;
    
    /**
     * The delegate processor that handles the actual message processing logic.
     */
    private final MessageProcessor delegateProcessor;
    
    /**
     * Constructs a RetryableMessageProcessor with the specified delegate processor.
     * 
     * @param sqsClient The SQS client for sending messages to DLQ
     * @param deadLetterQueueUrl The URL of the Dead Letter Queue
     * @param maxRetries The maximum number of retry attempts
     * @param delegateProcessor The delegate processor that performs the actual message processing
     */
    public RetryableMessageProcessor(AmazonSQS sqsClient, String deadLetterQueueUrl, 
                                    int maxRetries, MessageProcessor delegateProcessor) {
        if (sqsClient == null) {
            throw new IllegalArgumentException("SQS client cannot be null");
        }
        if (!StringUtils.hasText(deadLetterQueueUrl)) {
            throw new IllegalArgumentException("Dead letter queue URL cannot be null or empty");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        if (delegateProcessor == null) {
            throw new IllegalArgumentException("Delegate processor cannot be null");
        }
        
        this.sqsClient = sqsClient;
        this.deadLetterQueueUrl = deadLetterQueueUrl;
        this.maxRetries = maxRetries;
        this.delegateProcessor = delegateProcessor;
    }
    
    /**
     * Alternative constructor when no delegate processor is needed.
     * 
     * @param sqsClient The SQS client for sending messages to DLQ
     * @param deadLetterQueueUrl The URL of the Dead Letter Queue
     * @param maxRetries The maximum number of retry attempts
     */
    public RetryableMessageProcessor(AmazonSQS sqsClient, String deadLetterQueueUrl, int maxRetries) {
        this(sqsClient, deadLetterQueueUrl, maxRetries, null);
    }

    /**
     * Processes the message with retry capability. If processing fails,
     * retry count is checked, and the message is either retried or sent to
     * the Dead Letter Queue.
     * 
     * @param message the SQS message to process
     */
    @Override
    public void processMessage(SqsMessage message) {
        if (message == null) {
            logger.warn("Null message received for processing");
            return;
        }
        
        try {
            // If we have a delegate processor, use it; otherwise, subclasses should override this method
            if (delegateProcessor != null) {
                delegateProcessor.processMessage(message);
            } else {
                // FIXME: This should be implemented by subclasses if no delegate is provided
                throw new UnsupportedOperationException(
                    "No delegate processor provided. Either provide a delegate or override processMessage");
            }
            
            // If we reach here, processing was successful
            logger.debug("Successfully processed message: {}", message.getMessageId());
            
        } catch (Exception e) {
            handleProcessingException(message, e);
        }
    }
    
    /**
     * Handles exceptions that occur during message processing.
     * Determines if the message should be retried or sent to the DLQ.
     * 
     * @param message the message that failed processing
     * @param exception the exception that occurred during processing
     */
    protected void handleProcessingException(SqsMessage message, Exception exception) {
        int retryCount = getRetryCount(message);
        
        if (retryCount >= maxRetries) {
            // We've exceeded max retries, send to DLQ
            logger.warn("Max retry count ({}) exceeded for message: {}. Sending to DLQ.",
                      maxRetries, message.getMessageId());
            sendToDeadLetter(message, exception);
        } else {
            // TODO: Implement retry mechanism
            // This could be handled by putting the message back in the queue with
            // an updated retry count, or by using a separate retry queue
            logger.info("Processing failed for message: {}. Retry count: {}/{}",
                      message.getMessageId(), retryCount, maxRetries);
        }
    }
    
    /**
     * Extracts the current retry count from the message attributes.
     * 
     * @param message the message to check
     * @return the current retry count, or 0 if not present
     */
    private int getRetryCount(SqsMessage message) {
        String retryCountStr = message.getAttribute(RETRY_COUNT_ATTRIBUTE);
        if (retryCountStr != null) {
            try {
                return Integer.parseInt(retryCountStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid retry count format in message: {}", message.getMessageId());
            }
        }
        return 0;
    }

    /**
     * Sends a failed message to the Dead Letter Queue with error details.
     * 
     * @param message the failed message
     * @param exception the exception that caused the failure
     */
    public void sendToDeadLetter(SqsMessage message, Exception exception) {
        if (message == null) {
            logger.warn("Cannot send null message to Dead Letter Queue");
            return;
        }
        
        try {
            // Create message attributes for the DLQ message
            Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            
            // Add original message attributes
            for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                messageAttributes.put(entry.getKey(), 
                    new MessageAttributeValue().withDataType("String").withStringValue(entry.getValue()));
            }
            
            // Add error information
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            String errorType = exception != null ? exception.getClass().getName() : "Unknown";
            
            messageAttributes.put(ERROR_MESSAGE_ATTRIBUTE, 
                new MessageAttributeValue().withDataType("String").withStringValue(errorMessage));
            messageAttributes.put(ERROR_TYPE_ATTRIBUTE, 
                new MessageAttributeValue().withDataType("String").withStringValue(errorType));
            messageAttributes.put(RETRY_COUNT_ATTRIBUTE, 
                new MessageAttributeValue().withDataType("Number").withStringValue(String.valueOf(getRetryCount(message))));
            
            // Build the send message request
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(deadLetterQueueUrl)
                .withMessageBody(message.getBody())
                .withMessageAttributes(messageAttributes);
            
            // Send the message to the DLQ
            sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Message {} sent to Dead Letter Queue due to: {}", 
                     message.getMessageId(), errorMessage);
            
        } catch (Exception e) {
            logger.error("Failed to send message to Dead Letter Queue: {}", e.getMessage(), e);
            // Nothing more we can do here except log the error
        }
    }
}