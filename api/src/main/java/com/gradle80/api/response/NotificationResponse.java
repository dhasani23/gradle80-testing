package com.gradle80.api.response;

import com.gradle80.api.dto.NotificationDto;
import com.gradle80.api.model.ApiResponse;

/**
 * Notification operation response.
 * 
 * This class extends the base ApiResponse to include notification-specific data
 * in API responses related to notification operations.
 */
public class NotificationResponse extends ApiResponse {
    
    /**
     * The notification data associated with this response.
     */
    private NotificationDto notification;
    
    /**
     * Default constructor.
     * Initializes a response with default values.
     */
    public NotificationResponse() {
        super();
    }
    
    /**
     * Constructs a notification response with the given parameters.
     *
     * @param success      whether the operation was successful
     * @param message      response message
     * @param notification the notification data
     */
    public NotificationResponse(boolean success, String message, NotificationDto notification) {
        super(success, message);
        this.notification = notification;
    }
    
    /**
     * Factory method to create a successful notification response.
     *
     * @param notification the notification data
     * @param message      the success message
     * @return a new NotificationResponse instance with success=true
     */
    public static NotificationResponse success(NotificationDto notification, String message) {
        return new NotificationResponse(true, message, notification);
    }
    
    /**
     * Factory method to create an error notification response.
     *
     * @param message the error message
     * @return a new NotificationResponse instance with success=false
     */
    public static NotificationResponse error(String message) {
        return new NotificationResponse(false, message, null);
    }
    
    /**
     * Gets the notification data.
     *
     * @return the notification data
     */
    public NotificationDto getNotification() {
        return notification;
    }
    
    /**
     * Sets the notification data.
     *
     * @param notification the notification data
     */
    public void setNotification(NotificationDto notification) {
        this.notification = notification;
    }
    
    @Override
    public String toString() {
        return "NotificationResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", notification=" + notification +
                '}';
    }
    
    // TODO: Implement equals() and hashCode() methods if needed for comparison
    
    /**
     * Checks if this response contains notification data.
     * 
     * @return true if notification is not null, false otherwise
     */
    public boolean hasNotification() {
        return notification != null;
    }
}