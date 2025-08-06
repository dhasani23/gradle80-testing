package com.gradle80.aws.sqs;

import com.gradle80.aws.sqs.model.MessageProcessor;

/**
 * Interface for SQS listening operations.
 * This interface defines methods to start and stop listening to Amazon SQS queues.
 * 
 * Implementations of this interface should handle the connection to AWS SQS,
 * message consumption, and any necessary error handling.
 */
public interface SqsListener {
    
    /**
     * Start listening to messages from the specified SQS queue.
     * 
     * @param queueUrl The URL of the SQS queue to listen to
     * @throws IllegalArgumentException if queueUrl is null or empty
     * @throws IllegalStateException if the listener is already running for the specified queue
     */
    void startListening(String queueUrl);
    
    /**
     * Stop listening to messages from the specified SQS queue.
     * 
     * @param queueUrl The URL of the SQS queue to stop listening to
     * @throws IllegalArgumentException if queueUrl is null or empty
     * @throws IllegalStateException if no listener is currently running for the specified queue
     */
    void stopListening(String queueUrl);
    
    /**
     * Register a message processor for a specific SQS queue.
     * The processor will be invoked for each message received from the queue.
     *
     * @param queueUrl The URL of the SQS queue
     * @param processor The message processor to handle messages from this queue
     * @throws IllegalArgumentException if queueUrl is null or empty, or processor is null
     */
    void registerMessageProcessor(String queueUrl, MessageProcessor processor);
    
    // TODO: Consider adding method to check if listener is active for a specific queue
    
    // TODO: Consider adding configuration options for polling interval and visibility timeout
}