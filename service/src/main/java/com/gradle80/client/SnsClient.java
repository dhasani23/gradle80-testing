package com.gradle80.client;

import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Client for AWS SNS (Simple Notification Service) operations.
 */
@Component
public interface SnsClient {
    
    /**
     * Publishes a message to an SNS topic.
     *
     * @param topicArn the ARN of the SNS topic
     * @param message the message to publish
     * @param attributes optional message attributes
     * @return the message ID if successful, null otherwise
     */
    String publishMessage(String topicArn, String message, Map<String, String> attributes);
}