package com.gradle80.test.api;

import com.gradle80.api.dto.UserDto;
import com.gradle80.api.dto.OrderDto;
import com.gradle80.api.request.UserRequest;
import com.gradle80.api.request.OrderRequest;
import com.gradle80.api.response.UserResponse;
import com.gradle80.api.response.OrderResponse;
import com.gradle80.test.BaseIntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for API module.
 * Tests the User and Order API endpoints to verify correct functionality
 * and integration with other components.
 */
public class ApiIntegrationTest extends BaseIntegrationTest {

    /**
     * MockMvc instance for simulating HTTP requests.
     */
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Test the User API endpoints.
     * 
     * This test verifies:
     * 1. Creating a user
     * 2. Retrieving a user by ID
     * 3. Updating user information
     * 4. Deleting a user
     * 
     * @throws Exception if any test operations fail
     */
    @Test
    public void testUserEndpoint() throws Exception {
        // Create test user request
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");
        userRequest.setPassword("password123");
        
        // Test user creation
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
                
        UserResponse createResponse = fromJson(result.getResponse().getContentAsString(), UserResponse.class);
        assertNotNull("Created user should not be null", createResponse.getUser());
        Long userId = createResponse.getUser().getId();
        
        // Test retrieving user by ID
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.username").value("testuser"));
                
        // Test updating user
        userRequest.setFirstName("Updated");
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.firstName").value("Updated"));
                
        // Test deleting user
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
                
        // Verify user is deleted
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
                
        // TODO: Add validation for edge cases (invalid inputs, duplicate usernames)
        // FIXME: Handle potential race conditions in concurrent test executions
    }
    
    /**
     * Test the Order API endpoints.
     * 
     * This test verifies:
     * 1. Creating an order
     * 2. Retrieving an order by ID
     * 3. Retrieving orders by user ID
     * 4. Canceling an order
     *
     * @throws Exception if any test operations fail
     */
    @Test
    public void testOrderEndpoint() throws Exception {
        // First create a test user for order association
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("orderuser");
        userRequest.setEmail("orderuser@example.com");
        userRequest.setFirstName("Order");
        userRequest.setLastName("User");
        userRequest.setPassword("password123");
        
        MvcResult userResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userRequest)))
                .andExpect(status().isOk())
                .andReturn();
                
        UserResponse userResponse = fromJson(userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResponse.getUser().getId();
        
        // Create product IDs (assuming products already exist in the system)
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        
        // Create test order
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setProductIds(productIds);
        orderRequest.setShippingAddress("123 Test St, Test City, 12345");
        
        // Test order creation
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
                
        OrderResponse orderResponse = fromJson(result.getResponse().getContentAsString(), OrderResponse.class);
        Long orderId = orderResponse.getOrder().getId();
        
        // Test retrieving order by ID
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.order.id").value(orderId))
                .andExpect(jsonPath("$.order.userId").value(userId));
                
        // Test retrieving orders by user ID
        mockMvc.perform(get("/api/users/{id}/orders", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].order.id").value(orderId));
                
        // Test canceling an order
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.order.status").value("CANCELED"));
                
        // TODO: Add tests for order validation (invalid products, out of stock)
        // FIXME: Implement proper cleanup of test orders after test completion
    }
    
    /**
     * Custom setup steps for API tests
     */
    @Override
    protected void doSetUp() {
        // Any additional setup specific to API tests
        logger.info("Setting up API integration tests");
        
        // TODO: Implement pre-test data setup if needed
    }
    
    /**
     * Custom teardown steps for API tests
     */
    @Override
    protected void doTearDown() {
        // Clean up any resources or data created during tests
        logger.info("Tearing down API integration tests");
        
        // TODO: Implement cleanup of any remaining test data
    }
}