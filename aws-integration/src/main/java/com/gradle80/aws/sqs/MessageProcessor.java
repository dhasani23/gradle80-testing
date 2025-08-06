package com.gradle80.aws.sqs;

import com.gradle80.aws.sqs.model.SqsMessage;

/**
 * Interface for processing SQS messages.
 * 
 * Implementations of this interface are responsible for handling messages
 * received from Amazon SQS queues. Different processors can be registered
 * for different message types or queues.
 */
public interface MessageProcessor {
    
    /**
     * Process a message received from an SQS queue.
     * 
     * This method should handle the business logic associated with processing
     * the message content. Implementations might:
     * 
     * - Parse the message body (typically JSON)
     * - Validate the message content
     * - Perform business operations based on the message
     * - Handle any errors that occur during processing
     * 
     * Note: Implementations should be idempotent as SQS may deliver
     * messages multiple times.
     * 
     * @param message the SQS message to process
     * @throws IllegalArgumentException if the message format is invalid
     * @throws RuntimeException if processing fails for other reasons
     */
    void processMessage(SqsMessage message);
}