package com.gradle80.web.controller;

import com.gradle80.api.request.NotificationRequest;
import com.gradle80.api.response.NotificationResponse;
import com.gradle80.api.service.NotificationService;
import com.gradle80.web.filter.ResponseEnhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification operations.
 * 
 * Provides endpoints for sending notifications, retrieving user notifications,
 * and marking notifications as read.
 * 
 * @author Gradle80 Web Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;
    private final ResponseEnhancer responseEnhancer;
    
    /**
     * Constructor for dependency injection.
     *
     * @param notificationService the notification service
     * @param responseEnhancer the response enhancer
     */
    @Autowired
    public NotificationController(NotificationService notificationService, ResponseEnhancer responseEnhancer) {
        this.notificationService = notificationService;
        this.responseEnhancer = responseEnhancer;
    }
    
    /**
     * Sends a notification based on the provided request data.
     *
     * @param request the notification request containing user ID, type, and message
     * @return response entity with notification details
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody @Validated NotificationRequest request) {
        log.info("Received request to send notification: {}", request);
        
        // Validate request
        if (!request.validate()) {
            log.warn("Invalid notification request: {}", request);
            NotificationResponse errorResponse = NotificationResponse.error("Invalid notification request");
            return responseEnhancer.enhance(errorResponse, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // Process notification sending
            NotificationResponse response = notificationService.sendNotification(request);
            log.debug("Notification sent successfully: {}", response);
            
            // Return enhanced response
            return responseEnhancer.enhance(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error sending notification: ", e);
            NotificationResponse errorResponse = NotificationResponse.error("Failed to send notification: " + e.getMessage());
            return responseEnhancer.enhance(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return response entity with a list of notifications
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable Long userId) {
        log.info("Received request to get notifications for user ID: {}", userId);
        
        // Validate user ID
        if (userId == null || userId <= 0) {
            log.warn("Invalid user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Retrieve user notifications
            List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
            log.debug("Retrieved {} notifications for user ID: {}", notifications.size(), userId);
            
            // Return enhanced response
            return responseEnhancer.enhance(notifications, 
                    notifications.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving notifications for user ID {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification
     * @return response entity with updated notification details
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        log.info("Received request to mark notification ID {} as read", notificationId);
        
        // Validate notification ID
        if (notificationId == null || notificationId <= 0) {
            log.warn("Invalid notification ID: {}", notificationId);
            NotificationResponse errorResponse = NotificationResponse.error("Invalid notification ID");
            return responseEnhancer.enhance(errorResponse, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // Mark notification as read
            NotificationResponse response = notificationService.markAsRead(notificationId);
            
            if (response == null || !response.isSuccess()) {
                log.warn("Notification ID {} not found or could not be updated", notificationId);
                NotificationResponse errorResponse = NotificationResponse.error("Notification not found or could not be updated");
                return responseEnhancer.enhance(errorResponse, HttpStatus.NOT_FOUND);
            }
            
            log.debug("Notification ID {} marked as read successfully", notificationId);
            
            // Return enhanced response
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error marking notification ID {} as read: ", notificationId, e);
            NotificationResponse errorResponse = NotificationResponse.error("Failed to mark notification as read: " + e.getMessage());
            return responseEnhancer.enhance(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Handles batch notification operations.
     * 
     * TODO: Implement batch notification processing for improved performance with multiple notifications
     */
    
    /**
     * FIXME: Add support for notification filters by type, date range, and read status
     * Current implementation returns all notifications without filtering options
     */
}