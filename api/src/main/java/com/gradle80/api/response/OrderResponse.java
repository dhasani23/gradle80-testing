package com.gradle80.api.response;

import com.gradle80.api.dto.OrderDto;
import com.gradle80.api.model.ApiResponse;

/**
 * Order operation response.
 * This class extends ApiResponse to provide order-specific response data.
 * It contains order information returned from order-related operations.
 * 
 * @since 1.0
 */
public class OrderResponse extends ApiResponse {
    
    /**
     * The order data associated with this response.
     */
    private OrderDto order;
    
    /**
     * Default constructor.
     * Initializes an empty response with default values.
     */
    public OrderResponse() {
        super();
    }
    
    /**
     * Constructs a new order response with success status and message.
     * 
     * @param success whether the operation was successful
     * @param message response message
     */
    public OrderResponse(boolean success, String message) {
        super(success, message);
    }
    
    /**
     * Constructs a new order response with success status, message, and order data.
     * 
     * @param success whether the operation was successful
     * @param message response message
     * @param order the order data
     */
    public OrderResponse(boolean success, String message, OrderDto order) {
        super(success, message);
        this.order = order;
    }
    
    /**
     * Get order data
     * 
     * @return the order data
     */
    public OrderDto getOrder() {
        return order;
    }
    
    /**
     * Set order data
     * 
     * @param order the order data
     */
    public void setOrder(OrderDto order) {
        this.order = order;
    }
    
    /**
     * Factory method to create a successful order response.
     * 
     * @param message the success message
     * @param order the order data
     * @return a new OrderResponse instance with success=true
     */
    public static OrderResponse success(String message, OrderDto order) {
        return new OrderResponse(true, message, order);
    }
    
    /**
     * Factory method to create an error order response.
     * 
     * @param message the error message
     * @return a new OrderResponse instance with success=false and no order data
     */
    public static OrderResponse error(String message) {
        return new OrderResponse(false, message);
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", order=" + order +
                '}';
    }
    
    /**
     * Checks if this response contains order data.
     * 
     * @return true if the response contains order data, false otherwise
     */
    public boolean hasOrder() {
        return order != null;
    }
    
    /**
     * Creates a copy of this response with updated order data.
     * 
     * @param updatedOrder the updated order data
     * @return a new OrderResponse with the same status and message but updated order data
     */
    public OrderResponse withOrder(OrderDto updatedOrder) {
        return new OrderResponse(isSuccess(), getMessage(), updatedOrder);
    }
    
    // TODO: Implement equals() and hashCode() methods for proper comparison operations
    
    /**
     * Helper method to quickly check if an order has specific status
     * 
     * @param status the status to check against
     * @return true if the order has the specified status, false otherwise
     */
    public boolean hasStatus(String status) {
        if (order != null && order.getStatus() != null && status != null) {
            return order.getStatus().equalsIgnoreCase(status);
        }
        return false;
    }
    
    /**
     * FIXME: Consider adding validation logic to ensure order data consistency
     * when returning responses from service layer
     */
}