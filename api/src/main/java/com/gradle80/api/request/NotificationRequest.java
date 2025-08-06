package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;

/**
 * Notification creation request.
 * This class contains the data required to create a notification.
 */
public class NotificationRequest extends ApiRequest {
    
    /**
     * User identifier
     */
    private Long userId;
    
    /**
     * Notification type
     */
    private String type;
    
    /**
     * Notification message
     */
    private String message;
    
    /**
     * Default constructor
     */
    public NotificationRequest() {
        super();
    }
    
    /**
     * Constructor with all fields
     * 
     * @param userId the user identifier
     * @param type the notification type
     * @param message the notification message
     */
    public NotificationRequest(Long userId, String type, String message) {
        super();
        this.userId = userId;
        this.type = type;
        this.message = message;
    }
    
    /**
     * Gets the user identifier
     * 
     * @return the user identifier
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Sets the user identifier
     * 
     * @param userId the user identifier to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the notification type
     * 
     * @return the notification type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the notification type
     * 
     * @param type the notification type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Gets the notification message
     * 
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the notification message
     * 
     * @param message the notification message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Validates the request
     * Checks that:
     * - Base validation passes
     * - UserId is not null
     * - Type is not null or empty
     * - Message is not null or empty
     * 
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First check the base validation
        if (!super.validate()) {
            return false;
        }
        
        // Check that required fields are present
        if (userId == null) {
            return false;
        }
        
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        // TODO: Implement validation for specific notification types
        // Different notification types might require additional validation
        
        // FIXME: Add validation for message length - currently no limit
        
        return true;
    }
    
    @Override
    public String toString() {
        return "NotificationRequest{" +
                "requestId='" + getRequestId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}