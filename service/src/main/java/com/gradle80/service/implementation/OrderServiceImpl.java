package com.gradle80.service.implementation;

import com.gradle80.api.dto.OrderDto;
import com.gradle80.api.request.OrderRequest;
import com.gradle80.api.response.ApiResponse;
import com.gradle80.api.response.OrderResponse;
import com.gradle80.api.service.OrderService;
import com.gradle80.data.entity.OrderEntity;
import com.gradle80.data.entity.OrderItemEntity;
import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.exception.DataModuleException;
import com.gradle80.data.repository.OrderItemRepository;
import com.gradle80.data.repository.OrderRepository;
import com.gradle80.data.repository.ProductRepository;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.service.event.OrderCancelledEvent;
import com.gradle80.service.event.OrderCreatedEvent;
import com.gradle80.service.mapper.OrderMapper;
import com.gradle80.api.service.NotificationService;
import com.gradle80.api.request.NotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of order processing service.
 * Handles order creation, retrieval, cancellation and listing orders for users.
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Constructor for OrderServiceImpl with required dependencies.
     * 
     * @param orderRepository Repository for order operations
     * @param productRepository Repository for product operations
     * @param orderMapper Mapper for order entity/DTO conversions
     * @param notificationService Service for sending notifications
     * @param eventPublisher Publisher for order-related events
     * @param userRepository Repository for user operations
     * @param orderItemRepository Repository for order item operations
     */
    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper,
            NotificationService notificationService,
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Retrieves order information by ID.
     *
     * @param orderId Order identifier
     * @return OrderResponse containing order information
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        logger.info("Retrieving order with ID: {}", orderId);
        
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataModuleException("Order not found with ID: " + orderId));
        
        OrderDto orderDto = orderMapper.toDto(orderEntity);
        OrderResponse response = new OrderResponse();
        response.setSuccess(true);
        response.setOrder(orderDto);
        response.setMessage("Order retrieved successfully");
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }

    /**
     * Creates a new order.
     *
     * @param request Order creation request containing user ID, product IDs and shipping address
     * @return OrderResponse with the created order
     */
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        logger.info("Creating order for user ID: {}", request.getUserId());
        
        // Validate user existence
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataModuleException("User not found with ID: " + request.getUserId()));
        
        // Create order entity
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUser(user);
        orderEntity.setStatus("CREATED");
        orderEntity.setShippingAddress(request.getShippingAddress());
        orderEntity.setCreatedAt(new Date());
        orderEntity.setUpdatedAt(new Date());
        
        // Calculate total amount and create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new ArrayList<>();
        
        for (Long productId : request.getProductIds()) {
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataModuleException("Product not found with ID: " + productId));
            
            if (!product.getAvailable()) {
                throw new IllegalStateException("Product is not available: " + product.getName());
            }
            
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(orderEntity);
            item.setProduct(product);
            item.setQuantity(1); // Default quantity, could be extended in OrderRequest
            item.setPriceAtOrder(product.getPrice());
            
            orderItems.add(item);
            totalAmount = totalAmount.add(product.getPrice());
        }
        
        orderEntity.setTotalAmount(totalAmount);
        
        // Save order
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        
        // Associate order items with the saved order
        for (OrderItemEntity item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        
        // Publish event
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setUserId(user.getId());
        event.setTimestamp(System.currentTimeMillis());
        eventPublisher.publishEvent(event);
        
        // Send notification to user
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getId());
        notificationRequest.setType("ORDER_CREATED");
        notificationRequest.setMessage("Your order #" + savedOrder.getId() + " has been created successfully.");
        try {
            notificationService.sendNotification(notificationRequest);
        } catch (Exception e) {
            // Log but don't fail the order creation
            logger.error("Failed to send order creation notification", e);
        }
        
        // Prepare response
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        OrderResponse response = new OrderResponse();
        response.setSuccess(true);
        response.setOrder(orderDto);
        response.setMessage("Order created successfully");
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }

    /**
     * Cancels an existing order.
     *
     * @param orderId Order identifier
     * @return OrderResponse with the cancelled order
     */
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        logger.info("Cancelling order with ID: {}", orderId);
        
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataModuleException("Order not found with ID: " + orderId));
        
        // Validate that order can be cancelled
        if ("DELIVERED".equals(orderEntity.getStatus()) || "CANCELLED".equals(orderEntity.getStatus())) {
            throw new IllegalStateException("Order cannot be cancelled in status: " + orderEntity.getStatus());
        }
        
        // Update order status
        orderEntity.setStatus("CANCELLED");
        orderEntity.setUpdatedAt(new Date());
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        
        // Publish event
        OrderCancelledEvent event = new OrderCancelledEvent();
        event.setOrderId(savedOrder.getId());
        event.setReason("Cancelled by user");
        event.setTimestamp(System.currentTimeMillis());
        eventPublisher.publishEvent(event);
        
        // Send notification to user
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(orderEntity.getUser().getId());
        notificationRequest.setType("ORDER_CANCELLED");
        notificationRequest.setMessage("Your order #" + savedOrder.getId() + " has been cancelled.");
        try {
            notificationService.sendNotification(notificationRequest);
        } catch (Exception e) {
            // Log but don't fail the order cancellation
            logger.error("Failed to send order cancellation notification", e);
        }
        
        // Prepare response
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        OrderResponse response = new OrderResponse();
        response.setSuccess(true);
        response.setOrder(orderDto);
        response.setMessage("Order cancelled successfully");
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId User identifier
     * @return List of OrderResponse objects containing user's orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        logger.info("Retrieving orders for user ID: {}", userId);
        
        // Validate user existence
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataModuleException("User not found with ID: " + userId));
        
        // Get all orders for the user
        List<OrderEntity> userOrders = orderRepository.findByUser(user);
        
        // Map each order to response
        return userOrders.stream().map(order -> {
            OrderDto orderDto = orderMapper.toDto(order);
            OrderResponse response = new OrderResponse();
            response.setSuccess(true);
            response.setOrder(orderDto);
            response.setMessage("Order retrieved successfully");
            response.setTimestamp(System.currentTimeMillis());
            return response;
        }).collect(Collectors.toList());
    }
    
    // FIXME: Add pagination support for getUserOrders to handle large order volumes efficiently
    
    // TODO: Implement order update functionality to allow modifying shipping address before shipping
    
    // TODO: Add order tracking functionality with status updates
}