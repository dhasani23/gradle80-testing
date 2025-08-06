package com.gradle80.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for notification requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The user ID to send the notification to.
     */
    private Long userId;
    
    /**
     * The notification type (e.g., "EMAIL", "SMS", "PUSH").
     */
    private String type;
    
    /**
     * The notification message content.
     */
    private String message;
    
    /**
     * The notification title.
     */
    private String title;
    
    /**
     * The SNS topic ARN to publish to.
     */
    private String topicArn;
    
    /**
     * Custom attributes for the notification.
     */
    private Map<String, String> attributes;
}