package com.gradle80.aws.sqs.model;

/**
 * Interface for SQS message processing.
 * Implementations of this interface handle messages received from Amazon SQS queues.
 */
public interface MessageProcessor {
    
    /**
     * Process a received SQS message.
     * 
     * @param message The SQS message to process
     */
    void processMessage(SqsMessage message);
}