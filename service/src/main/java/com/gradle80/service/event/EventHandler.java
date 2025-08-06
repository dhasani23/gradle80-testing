package com.gradle80.service.event;

/**
 * Interface for handling application events.
 * 
 * Implementations of this interface will be responsible for processing
 * various events generated within the application such as OrderCreatedEvent,
 * OrderCancelledEvent, UserCreatedEvent, etc.
 * 
 * Event handlers can perform actions like sending notifications,
 * updating cache, logging, or triggering additional processes.
 *
 * @see OrderEventHandler
 * @see UserEventHandler
 */
public interface EventHandler {
    
    /**
     * Handles an application event.
     * 
     * Implementations should check the type of the event and delegate to 
     * appropriate handling methods. For example:
     * 
     * <pre>
     * public void handleEvent(Object event) {
     *     if (event instanceof OrderCreatedEvent) {
     *         handleOrderCreatedEvent((OrderCreatedEvent) event);
     *     } else if (event instanceof UserCreatedEvent) {
     *         handleUserCreatedEvent((UserCreatedEvent) event);
     *     }
     *     // etc.
     * }
     * </pre>
     * 
     * @param event The event to handle
     */
    void handleEvent(Object event);
}