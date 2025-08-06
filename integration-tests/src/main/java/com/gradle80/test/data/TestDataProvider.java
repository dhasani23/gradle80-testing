package com.gradle80.test.data;

import com.gradle80.service.domain.User;

/**
 * Interface for providing test data for integration tests.
 * Implementations of this interface should create and manage test data
 * for various test scenarios.
 */
public interface TestDataProvider {
    
    /**
     * Creates a test user with the specified username.
     * 
     * @param username the username for the test user
     * @return the created User object
     */
    User createTestUser(String username);
    
    /**
     * Creates a test order for the specified user.
     * 
     * @param userId the ID of the user for whom to create the order
     * @return the created Order object
     */
    Order createTestOrder(Long userId);
}