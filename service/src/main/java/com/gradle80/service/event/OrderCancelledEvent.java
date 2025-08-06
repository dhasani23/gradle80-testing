package com.gradle80.service.event;

import java.util.Objects;

/**
 * Event representing order cancellation.
 * This class is used to notify the system when an order has been cancelled.
 */
public class OrderCancelledEvent {

    private Long orderId;        // Order identifier
    private String reason;       // Cancellation reason
    private Long timestamp;      // Event timestamp

    /**
     * Default constructor.
     */
    public OrderCancelledEvent() {
        // Default constructor
    }

    /**
     * Constructs an OrderCancelledEvent with all required fields.
     *
     * @param orderId   the order identifier
     * @param reason    the cancellation reason
     * @param timestamp the event timestamp
     */
    public OrderCancelledEvent(Long orderId, String reason, Long timestamp) {
        this.orderId = orderId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    /**
     * Gets the order identifier.
     *
     * @return the order identifier
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order identifier.
     *
     * @param orderId the order identifier
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the cancellation reason.
     *
     * @return the cancellation reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the cancellation reason.
     *
     * @param reason the cancellation reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the event timestamp.
     *
     * @return the event timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the event timestamp.
     *
     * @param timestamp the event timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCancelledEvent that = (OrderCancelledEvent) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(reason, that.reason) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, reason, timestamp);
    }

    @Override
    public String toString() {
        return "OrderCancelledEvent{" +
                "orderId=" + orderId +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}