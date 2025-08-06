package com.gradle80.service.implementation;

import com.gradle80.service.NotificationService;
import com.gradle80.repository.NotificationRepository;
import com.gradle80.mapper.NotificationMapper;
import com.gradle80.client.SnsClient;
import com.gradle80.model.NotificationRequest;
import com.gradle80.model.NotificationResponse;
import com.gradle80.entity.Notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the NotificationService interface.
 * This service handles notification operations including sending, retrieving and updating notifications.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SnsClient snsClient;

    /**
     * Constructor for dependency injection.
     *
     * @param notificationRepository Repository for notification persistence operations
     * @param notificationMapper Mapper for entity-dto conversions
     * @param snsClient Client for AWS SNS operations
     */
    @Autowired
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            SnsClient snsClient) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.snsClient = snsClient;
    }

    /**
     * Sends a notification through SNS and persists it in the database.
     *
     * @param request the notification request data
     * @return the created notification as a response
     */
    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        logger.info("Sending notification: {}", request);
        
        try {
            // Convert request to entity
            Notification notification = notificationMapper.requestToEntity(request);
            
            // Set default values
            notification.setRead(false);
            notification.setCreatedAt(System.currentTimeMillis());
            
            // Persist notification
            Notification savedNotification = notificationRepository.save(notification);
            
            // Send notification via SNS
            String messageId = snsClient.publishMessage(
                request.getTopicArn(), 
                request.getMessage(),
                generateMessageAttributes(request)
            );
            
            // Update notification with message ID
            if (messageId != null) {
                savedNotification.setExternalId(messageId);
                savedNotification = notificationRepository.save(savedNotification);
            } else {
                logger.warn("Failed to get message ID from SNS for notification: {}", savedNotification.getId());
                // TODO: Implement retry logic for failed SNS publishing
            }
            
            return notificationMapper.entityToResponse(savedNotification);
        } catch (Exception e) {
            logger.error("Error sending notification", e);
            // FIXME: Current implementation swallows exceptions, should implement proper error handling
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return list of notification responses
     */
    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        logger.info("Getting notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        
        // Apply sorting - most recent notifications first
        notifications.sort((n1, n2) -> Long.compare(n2.getCreatedAt(), n1.getCreatedAt()));
        
        // Map entities to response objects
        return notifications.stream()
                .map(notificationMapper::entityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification to update
     * @return updated notification response or null if not found
     */
    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        logger.info("Marking notification as read: {}", notificationId);
        
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (!notificationOpt.isPresent()) {
            logger.warn("Notification not found with ID: {}", notificationId);
            // TODO: Consider throwing a specific NotificationNotFoundException instead of returning null
            return null;
        }
        
        Notification notification = notificationOpt.get();
        
        // Only update if not already read
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(System.currentTimeMillis());
            notification = notificationRepository.save(notification);
            logger.debug("Notification marked as read: {}", notificationId);
        } else {
            logger.debug("Notification was already marked as read: {}", notificationId);
        }
        
        return notificationMapper.entityToResponse(notification);
    }
    
    /**
     * Helper method to generate message attributes for SNS.
     *
     * @param request the notification request
     * @return a map of message attributes
     */
    private java.util.Map<String, String> generateMessageAttributes(NotificationRequest request) {
        java.util.Map<String, String> attributes = new java.util.HashMap<>();
        
        // Add standard attributes
        attributes.put("application", "gradle80");
        attributes.put("environment", System.getProperty("app.environment", "dev"));
        attributes.put("type", request.getType());
        
        // Add custom attributes if available
        if (request.getAttributes() != null) {
            attributes.putAll(request.getAttributes());
        }
        
        return attributes;
    }
}