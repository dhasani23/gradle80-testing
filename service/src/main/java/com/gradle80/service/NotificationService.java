package com.gradle80.service;

import com.gradle80.model.NotificationRequest;
import com.gradle80.model.NotificationResponse;

import java.util.List;

/**
 * Service interface for notification operations.
 */
public interface NotificationService {
    
    /**
     * Sends a notification.
     *
     * @param request the notification request data
     * @return the created notification as a response
     */
    NotificationResponse sendNotification(NotificationRequest request);
    
    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return list of notification responses
     */
    List<NotificationResponse> getUserNotifications(Long userId);
    
    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification to update
     * @return updated notification response
     */
    NotificationResponse markAsRead(Long notificationId);
}