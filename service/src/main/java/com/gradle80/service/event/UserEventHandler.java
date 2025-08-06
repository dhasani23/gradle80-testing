package com.gradle80.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.service.aws.SnsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handler for user-related events.
 * 
 * This service is responsible for processing events related to user management
 * such as user creation and deletion. For each event type, it publishes
 * appropriate notifications to SNS topics for downstream processing.
 */
@Service
public class UserEventHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserEventHandler.class);
    
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a UserEventHandler with the required dependencies.
     *
     * @param snsClient the SNS client used to publish event notifications
     */
    @Autowired
    public UserEventHandler(SnsClient snsClient) {
        this.snsClient = snsClient;
        this.objectMapper = new ObjectMapper();
        logger.info("Initialized UserEventHandler");
    }
    
    /**
     * Handles a user-related event by delegating to the appropriate handler method.
     * 
     * @param event the event to handle
     */
    @Override
    public void handleEvent(Object event) {
        logger.debug("Handling event: {}", event);
        
        if (event instanceof UserCreatedEvent) {
            handleUserCreatedEvent((UserCreatedEvent) event);
        } else if (event instanceof UserDeletedEvent) {
            handleUserDeletedEvent((UserDeletedEvent) event);
        } else {
            logger.warn("Unsupported event type: {}", event.getClass().getName());
        }
    }
    
    /**
     * Handles the user creation event.
     * 
     * Publishes a notification to the configured SNS topic with information
     * about the newly created user.
     * 
     * @param event the user created event
     */
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        logger.info("Handling UserCreatedEvent for user ID: {}, username: {}", 
                event.getUserId(), event.getUsername());
        
        try {
            String message = objectMapper.writeValueAsString(event);
            String subject = "User Created: " + event.getUsername();
            
            String messageId = snsClient.publishMessage(subject, message);
            logger.info("Published UserCreatedEvent notification, message ID: {}", messageId);
            
            // TODO: Consider adding additional processing like audit logging or cache updates
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize UserCreatedEvent", e);
            // FIXME: Add retry mechanism or dead-letter queue for failed events
        } catch (Exception e) {
            logger.error("Error handling UserCreatedEvent", e);
            // FIXME: Implement proper error handling and circuit breaking
        }
    }
    
    /**
     * Handles the user deletion event.
     * 
     * Publishes a notification to the configured SNS topic with information
     * about the deleted user.
     * 
     * @param event the user deleted event
     */
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        logger.info("Handling UserDeletedEvent for user ID: {}", event.getUserId());
        
        try {
            String message = objectMapper.writeValueAsString(event);
            String subject = "User Deleted: " + event.getUserId();
            
            String messageId = snsClient.publishMessage(subject, message);
            logger.info("Published UserDeletedEvent notification, message ID: {}", messageId);
            
            // TODO: Implement cleanup logic for user-related data
            // TODO: Consider notifying other services about user deletion
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize UserDeletedEvent", e);
            // FIXME: Consider using a more robust serialization method
        } catch (Exception e) {
            logger.error("Error handling UserDeletedEvent", e);
            // FIXME: Improve error recovery strategies
        }
    }
}