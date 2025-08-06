package com.gradle80.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for notification responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The notification ID.
     */
    private Long id;
    
    /**
     * The user ID the notification was sent to.
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
     * Whether the notification has been read.
     */
    private boolean read;
    
    /**
     * When the notification was created.
     */
    private Long createdAt;
    
    /**
     * When the notification was read.
     */
    private Long readAt;
    
    /**
     * External ID (e.g., SNS message ID).
     */
    private String externalId;
    
    /**
     * Custom attributes for the notification.
     */
    private Map<String, String> attributes;
}