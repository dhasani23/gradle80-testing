package com.gradle80.service.event;

import com.gradle80.api.request.NotificationRequest;
import com.gradle80.api.service.NotificationService;
import com.gradle80.service.aws.SnsClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handler for order-related events.
 * 
 * This service implements the EventHandler interface and processes order-related events
 * such as OrderCreatedEvent and OrderCancelledEvent. It notifies users about order status
 * changes through the notification service and publishes events to SNS for other systems.
 */
@Service
public class OrderEventHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);
    
    private final SnsClient snsClient;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs an OrderEventHandler with the required dependencies.
     *
     * @param snsClient The SNS client for publishing events to AWS SNS
     * @param notificationService The notification service for sending user notifications
     * @param objectMapper The object mapper for JSON serialization
     */
    @Autowired
    public OrderEventHandler(
            SnsClient snsClient,
            NotificationService notificationService,
            ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        logger.info("OrderEventHandler initialized");
    }

    /**
     * Handles various types of events related to orders.
     * This method identifies the type of event and delegates to appropriate handler methods.
     *
     * @param event The event object to handle
     */
    @Override
    public void handleEvent(Object event) {
        logger.debug("Handling event: {}", event);
        
        try {
            if (event instanceof OrderCreatedEvent) {
                handleOrderCreatedEvent((OrderCreatedEvent) event);
            } else if (event instanceof OrderCancelledEvent) {
                handleOrderCancelledEvent((OrderCancelledEvent) event);
            } else {
                logger.warn("Unsupported event type: {}", event.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error handling event: {}", event, e);
            // TODO: Implement retry mechanism for failed event handling
        }
    }

    /**
     * Handles order creation events by notifying the user and publishing to SNS.
     *
     * @param event The order created event
     */
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Handling order created event for orderId: {}, userId: {}", 
                event.getOrderId(), event.getUserId());
        
        try {
            // Create and send notification to the user
            NotificationRequest notificationRequest = createOrderCreatedNotification(event);
            notificationService.sendNotification(notificationRequest);
            
            // Publish event to SNS for external systems
            String eventJson = objectMapper.writeValueAsString(event);
            snsClient.publishMessage("OrderCreated", eventJson);
            
            logger.debug("Successfully processed order created event for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to process order created event for orderId: {}", event.getOrderId(), e);
            // FIXME: Add dead letter queue handling for failed events
            throw new RuntimeException("Failed to process order creation event", e);
        }
    }

    /**
     * Handles order cancellation events by notifying the user and publishing to SNS.
     *
     * @param event The order cancelled event
     */
    public void handleOrderCancelledEvent(OrderCancelledEvent event) {
        logger.info("Handling order cancelled event for orderId: {}, reason: {}", 
                event.getOrderId(), event.getReason());
        
        try {
            // Create and send notification
            NotificationRequest notificationRequest = createOrderCancelledNotification(event);
            notificationService.sendNotification(notificationRequest);
            
            // Publish event to SNS
            String eventJson = objectMapper.writeValueAsString(event);
            snsClient.publishMessage("OrderCancelled", eventJson);
            
            logger.debug("Successfully processed order cancelled event for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to process order cancelled event for orderId: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to process order cancellation event", e);
        }
    }
    
    /**
     * Creates a notification request for order creation events.
     * 
     * @param event The order created event
     * @return A notification request
     */
    private NotificationRequest createOrderCreatedNotification(OrderCreatedEvent event) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(event.getUserId());
        request.setType("ORDER_CREATED");
        request.setMessage(String.format("Your order #%d has been created successfully.", 
                event.getOrderId()));
        return request;
    }
    
    /**
     * Creates a notification request for order cancellation events.
     * 
     * @param event The order cancelled event
     * @return A notification request
     */
    private NotificationRequest createOrderCancelledNotification(OrderCancelledEvent event) {
        // Note: Since OrderCancelledEvent doesn't contain userId, we would need to
        // look it up from the database in a real implementation.
        // TODO: Fetch userId from order repository based on orderId
        Long userId = null; // Placeholder for actual userId
        
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setType("ORDER_CANCELLED");
        request.setMessage(String.format("Your order #%d has been cancelled. Reason: %s", 
                event.getOrderId(), event.getReason()));
        return request;
    }
}