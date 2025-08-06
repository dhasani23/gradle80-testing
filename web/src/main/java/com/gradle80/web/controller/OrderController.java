package com.gradle80.web.controller;

import com.gradle80.api.request.OrderRequest;
import com.gradle80.api.response.OrderResponse;
import com.gradle80.api.service.OrderService;
import com.gradle80.web.filter.ResponseEnhancer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for order operations.
 * Provides endpoints for creating, retrieving and cancelling orders.
 */
@RestController
@RequestMapping("/api/orders")
@Api(value = "Order Management", description = "Operations pertaining to orders in the application")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final ResponseEnhancer responseEnhancer;

    /**
     * Retrieves an order by its ID.
     * 
     * @param orderId the ID of the order to retrieve
     * @return the order response wrapped in a ResponseEntity
     */
    @GetMapping("/{orderId}")
    @ApiOperation(value = "Get an order by its ID", response = OrderResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved order"),
        @ApiResponse(code = 404, message = "Order not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @ApiParam(value = "Order ID", required = true)
            @PathVariable Long orderId) {
        
        log.debug("REST request to get order with ID: {}", orderId);
        OrderResponse response = orderService.getOrderById(orderId);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }

    /**
     * Creates a new order.
     *
     * @param request the order request containing order details
     * @return the created order response wrapped in a ResponseEntity
     */
    @PostMapping
    @ApiOperation(value = "Create a new order", response = OrderResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Order successfully created"),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @ApiParam(value = "Order request", required = true)
            @Valid @RequestBody OrderRequest request) {
        
        log.debug("REST request to create a new order: {}", request);
        OrderResponse response = orderService.createOrder(request);
        return responseEnhancer.enhance(response, HttpStatus.CREATED);
    }

    /**
     * Cancels an existing order by its ID.
     *
     * @param orderId the ID of the order to cancel
     * @return the updated order response wrapped in a ResponseEntity
     */
    @DeleteMapping("/{orderId}/cancel")
    @ApiOperation(value = "Cancel an order", response = OrderResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully cancelled"),
        @ApiResponse(code = 404, message = "Order not found"),
        @ApiResponse(code = 400, message = "Order cannot be cancelled"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @ApiParam(value = "Order ID", required = true)
            @PathVariable Long orderId) {
        
        log.debug("REST request to cancel order with ID: {}", orderId);
        OrderResponse response = orderService.cancelOrder(orderId);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of orders wrapped in a ResponseEntity
     */
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "Get all orders for a user", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved orders"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @ApiParam(value = "User ID", required = true)
            @PathVariable Long userId) {
        
        log.debug("REST request to get all orders for user with ID: {}", userId);
        
        // TODO: Add pagination support for large order sets
        List<OrderResponse> responses = orderService.getUserOrders(userId);
        
        // FIXME: Performance optimization needed when retrieving many orders
        return responseEnhancer.enhance(responses, HttpStatus.OK);
    }
}