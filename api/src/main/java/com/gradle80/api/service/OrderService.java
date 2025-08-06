package com.gradle80.api.service;

import com.gradle80.api.request.OrderRequest;
import com.gradle80.api.response.OrderResponse;

import java.util.List;

/**
 * Order processing service interface.
 * This interface defines the contract for order processing operations including
 * order retrieval, creation, cancellation, and listing user orders.
 *
 * @since 1.0
 */
public interface OrderService {

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the unique identifier of the order
     * @return an OrderResponse containing the order details if found, or error information if not found
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Creates a new order in the system.
     *
     * @param request the OrderRequest containing order details such as user ID, product IDs, and shipping address
     * @return an OrderResponse containing the created order details or error information
     */
    OrderResponse createOrder(OrderRequest request);

    /**
     * Cancels an existing order.
     * This will change the order status to "CANCELLED" if the order is in a cancellable state.
     *
     * @param orderId the unique identifier of the order to cancel
     * @return an OrderResponse containing the updated order details or error information
     */
    OrderResponse cancelOrder(Long orderId);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a List of OrderResponse objects containing all orders associated with the user
     */
    List<OrderResponse> getUserOrders(Long userId);
    
    /**
     * TODO: Implement method to update order status for order processing workflow
     */
    
    /**
     * TODO: Add method to support order filtering by date range and status
     */
    
    /**
     * FIXME: Consider adding pagination support for getUserOrders method
     * to handle large number of orders efficiently
     */
}