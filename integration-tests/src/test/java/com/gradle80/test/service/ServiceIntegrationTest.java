package com.gradle80.test.service;

import com.gradle80.api.request.OrderRequest;
import com.gradle80.api.request.UserRequest;
import com.gradle80.api.response.OrderResponse;
import com.gradle80.api.response.UserResponse;
import com.gradle80.api.service.OrderService;
import com.gradle80.api.service.UserService;
import com.gradle80.service.domain.User;
import com.gradle80.test.BaseIntegrationTest;
import com.gradle80.test.data.Order;
import com.gradle80.test.data.TestDataProvider;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 * Integration tests for service module.
 * Tests user and order service functionality in an integrated environment.
 */
@ContextConfiguration(classes = {ServiceTestConfig.class})
public class ServiceIntegrationTest extends BaseIntegrationTest {
    
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "testuser@example.com";
    
    /**
     * User service to be tested
     */
    @Autowired
    private UserService userService;
    
    /**
     * Order service to be tested
     */
    @Autowired
    private OrderService orderService;
    
    /**
     * Test data provider for creating test entities
     */
    @Autowired
    private TestDataProvider testDataProvider;
    
    /**
     * Tests user creation functionality.
     * Verifies that a user can be successfully created and retrieved.
     */
    @Test
    public void testUserCreation() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("securePassword123");
        
        // Act
        UserResponse creationResponse = userService.createUser(request);
        
        // Assert creation
        Assert.assertTrue("User creation should succeed", creationResponse.isSuccess());
        Assert.assertNotNull("User ID should be assigned", creationResponse.getUser().getId());
        Assert.assertEquals("Username should match", TEST_USERNAME, creationResponse.getUser().getUsername());
        
        // Verify retrieval
        Long userId = creationResponse.getUser().getId();
        UserResponse retrievalResponse = userService.getUserById(userId);
        
        Assert.assertTrue("User retrieval should succeed", retrievalResponse.isSuccess());
        Assert.assertEquals("User ID should match", userId, retrievalResponse.getUser().getId());
        Assert.assertEquals("Email should match", TEST_EMAIL, retrievalResponse.getUser().getEmail());
        
        // TODO: Add verification for user update functionality
    }
    
    /**
     * Tests order processing functionality.
     * Verifies order creation, retrieval, and cancellation.
     */
    @Test
    public void testOrderProcessing() {
        // Arrange - Create a test user first
        User testUser = testDataProvider.createTestUser("orderuser");
        Long userId = testUser.getId();
        
        // Create product IDs for order
        // In a real test, we would create actual products, but for this test we'll use test data
        List<Long> productIds = Arrays.asList(1L, 2L);
        
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setProductIds(productIds);
        orderRequest.setShippingAddress("123 Test Street, Test City, TS 12345");
        
        // Act - Create order
        OrderResponse creationResponse = orderService.createOrder(orderRequest);
        
        // Assert order creation
        Assert.assertTrue("Order creation should succeed", creationResponse.isSuccess());
        Assert.assertNotNull("Order ID should be assigned", creationResponse.getOrder().getId());
        Assert.assertEquals("Order status should be CREATED", "CREATED", creationResponse.getOrder().getStatus());
        
        // Get order by ID
        Long orderId = creationResponse.getOrder().getId();
        OrderResponse retrievalResponse = orderService.getOrderById(orderId);
        
        // Assert order retrieval
        Assert.assertTrue("Order retrieval should succeed", retrievalResponse.isSuccess());
        Assert.assertEquals("Order ID should match", orderId, retrievalResponse.getOrder().getId());
        
        // Get all user orders
        List<OrderResponse> userOrdersResponse = orderService.getUserOrders(userId);
        
        // Assert user orders retrieval
        Assert.assertFalse("User should have orders", userOrdersResponse.isEmpty());
        Assert.assertTrue("User orders should contain the created order", 
            userOrdersResponse.stream().anyMatch(response -> 
                response.getOrder().getId().equals(orderId)));
        
        // Cancel order
        OrderResponse cancellationResponse = orderService.cancelOrder(orderId);
        
        // Assert order cancellation
        Assert.assertTrue("Order cancellation should succeed", cancellationResponse.isSuccess());
        Assert.assertEquals("Order status should be CANCELLED", "CANCELLED", cancellationResponse.getOrder().getStatus());
        
        // FIXME: There's an issue with order cancellation when the order is already shipped
        // Need to handle this case better in the OrderService implementation
    }
    
    @Override
    protected void doSetUp() {
        // Any specific setup for service tests can go here
        logger.info("Setting up service integration test");
    }
    
    @Override
    protected void doTearDown() {
        // Clean up any resources specific to these tests
        logger.info("Tearing down service integration test");
    }
}