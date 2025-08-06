package com.gradle80.aws.sns;

import com.amazonaws.services.sns.model.PublishResult;
import java.util.Map;

/**
 * Interface for SNS publishing operations.
 * <p>
 * This interface defines the contract for publishing messages to Amazon SNS topics.
 * It provides methods for simple message publishing as well as publishing with
 * message attributes for more complex scenarios.
 * </p>
 * 
 * @since 1.0
 */
public interface SnsPublisher {
    
    /**
     * Publishes a message to the specified SNS topic.
     * <p>
     * The message object will be serialized to JSON before publishing.
     * </p>
     * 
     * @param topicArn The ARN of the SNS topic to publish to
     * @param message The message object to be published
     * @return A PublishResult containing the message ID and other metadata
     * @throws com.amazonaws.AmazonServiceException If the message cannot be published due to service issues
     * @throws com.amazonaws.AmazonClientException If the message cannot be published due to client issues
     */
    PublishResult publishMessage(String topicArn, Object message);
    
    /**
     * Publishes a message with additional attributes to the specified SNS topic.
     * <p>
     * The message object will be serialized to JSON before publishing.
     * Message attributes can be used for message filtering and additional metadata.
     * </p>
     * 
     * @param topicArn The ARN of the SNS topic to publish to
     * @param message The message object to be published
     * @param attributes A map of attribute name/value pairs to attach to the message
     * @return A PublishResult containing the message ID and other metadata
     * @throws com.amazonaws.AmazonServiceException If the message cannot be published due to service issues
     * @throws com.amazonaws.AmazonClientException If the message cannot be published due to client issues
     * @throws IllegalArgumentException If the attributes map contains invalid values
     */
    PublishResult publishMessageWithAttributes(String topicArn, Object message, Map<String, String> attributes);
    
    // TODO: Add method for batch publishing messages for improved throughput
}