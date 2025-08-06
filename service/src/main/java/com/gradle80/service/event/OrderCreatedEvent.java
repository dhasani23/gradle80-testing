package com.gradle80.service.event;

import java.util.Objects;

/**
 * Event representing order creation.
 * This class encapsulates all necessary information about a newly created order
 * to be used by event handlers and notification services.
 */
public class OrderCreatedEvent {

    /**
     * Order identifier
     */
    private Long orderId;
    
    /**
     * User identifier (customer who placed the order)
     */
    private Long userId;
    
    /**
     * Event timestamp in milliseconds since epoch
     */
    private Long timestamp;

    /**
     * No-arg constructor for frameworks requiring it
     */
    public OrderCreatedEvent() {
        // Default constructor for serialization frameworks
    }
    
    /**
     * Constructor with all required fields
     *
     * @param orderId   the order identifier
     * @param userId    the user identifier
     * @param timestamp the event timestamp
     */
    public OrderCreatedEvent(Long orderId, Long userId, Long timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    /**
     * Creates a new OrderCreatedEvent with the current timestamp
     *
     * @param orderId the order identifier
     * @param userId  the user identifier
     * @return a new OrderCreatedEvent instance
     */
    public static OrderCreatedEvent create(Long orderId, Long userId) {
        return new OrderCreatedEvent(orderId, userId, System.currentTimeMillis());
    }

    /**
     * @return the order identifier
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the order identifier to set
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    /**
     * @return the user identifier
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the user identifier to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * @return the event timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the event timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, userId, timestamp);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}