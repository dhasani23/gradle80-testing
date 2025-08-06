package com.gradle80.api.service;

import com.gradle80.api.request.NotificationRequest;
import com.gradle80.api.response.NotificationResponse;

import java.util.List;

/**
 * Notification service interface.
 * 
 * This interface defines the contract for operations related to notifications management
 * in the system. It includes methods for sending notifications, retrieving user notifications,
 * and marking notifications as read.
 */
public interface NotificationService {
    
    /**
     * Sends a notification based on the provided request.
     * 
     * @param request the notification request containing user ID, type, and message
     * @return a notification response with the created notification details
     * @throws IllegalArgumentException if the request is invalid
     */
    NotificationResponse sendNotification(NotificationRequest request);
    
    /**
     * Retrieves all notifications for a specific user.
     * 
     * @param userId the ID of the user whose notifications are to be retrieved
     * @return a list of notification responses containing all user notifications
     * @throws IllegalArgumentException if userId is null or invalid
     */
    List<NotificationResponse> getUserNotifications(Long userId);
    
    /**
     * Marks a notification as read.
     * 
     * @param notificationId the ID of the notification to be marked as read
     * @return a notification response with the updated notification details
     * @throws IllegalArgumentException if notificationId is null or invalid
     * @throws java.util.NoSuchElementException if notification doesn't exist
     */
    NotificationResponse markAsRead(Long notificationId);
    
    // TODO: Add method to retrieve notifications by type
    
    // TODO: Add method to mark all user notifications as read
    
    // FIXME: Consider adding pagination support for getUserNotifications method
    // to handle users with large numbers of notifications
}