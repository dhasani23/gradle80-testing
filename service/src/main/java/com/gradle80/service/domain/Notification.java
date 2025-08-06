package com.gradle80.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Domain class representing a notification in the application.
 * This is the core business representation of a notification, independent of
 * persistence or presentation concerns.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    /**
     * Unique identifier for the notification
     */
    private Long id;
    
    /**
     * The user ID of the notification recipient
     */
    private Long userId;
    
    /**
     * The type of notification (e.g., ORDER_CONFIRMATION, PAYMENT_RECEIVED, etc.)
     */
    private String type;
    
    /**
     * The message content of the notification
     */
    private String message;
    
    /**
     * Flag indicating whether the notification has been read by the user
     */
    private boolean read;
    
    /**
     * Timestamp when the notification was created
     */
    private Date createdAt;
    
    /**
     * Marks the notification as read
     * 
     * @return this notification instance for method chaining
     */
    public Notification markAsRead() {
        this.read = true;
        return this;
    }
    
    /**
     * Checks if this notification is for a specific user
     * 
     * @param userId the user ID to check
     * @return true if the notification is for the specified user, false otherwise
     */
    public boolean isForUser(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
}