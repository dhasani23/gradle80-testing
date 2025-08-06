package com.gradle80.test.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.service.domain.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the TestDataProvider interface.
 * Provides methods for creating test data used in integration tests.
 */
@Component
public class TestDataProviderImpl implements TestDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(TestDataProviderImpl.class);
    
    private static final String[] DEFAULT_ADDRESSES = {
        "123 Test St., Test City, 12345",
        "456 Mock Ave., Sample Town, 54321",
        "789 Integration Rd., Test Valley, 67890"
    };
    
    private static final AtomicLong orderIdGenerator = new AtomicLong(1000);
    private static final Random random = new Random();
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a test user with the specified username.
     * If a user with this username already exists, it returns that user.
     * 
     * @param username the username for the test user
     * @return the created User object
     */
    @Override
    public User createTestUser(String username) {
        logger.info("Creating test user with username: {}", username);
        
        // Check if user already exists
        UserEntity existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            logger.debug("User {} already exists, returning existing user", username);
            return convertToUserDomain(existingUser);
        }
        
        // Generate email based on username
        String email = username + "@test.com";
        
        // Create new user entity
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
        userEntity.setPasswordHash(UUID.randomUUID().toString()); // Mock password hash
        userEntity.setActive(true);
        userEntity.setCreatedAt(new Date());
        userEntity.setUpdatedAt(new Date());
        
        try {
            UserEntity savedUser = userRepository.save(userEntity);
            logger.debug("Successfully created test user: {}", savedUser.getUsername());
            return convertToUserDomain(savedUser);
        } catch (Exception e) {
            logger.error("Failed to create test user", e);
            // Fallback: create an in-memory user
            logger.warn("Creating in-memory user as fallback");
            return createInMemoryUser(username);
        }
    }

    /**
     * Creates a test order for the specified user.
     * This will generate an order with random products.
     * 
     * @param userId the ID of the user for whom to create the order
     * @return the created Order object
     */
    @Override
    public Order createTestOrder(Long userId) {
        logger.info("Creating test order for user ID: {}", userId);
        
        // Verify user exists
        if (userId == null) {
            logger.error("Cannot create order for null user ID");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Generate synthetic order data
        Long orderId = orderIdGenerator.incrementAndGet();
        List<Long> productIds = generateRandomProductIds();
        BigDecimal totalAmount = calculateTotalAmount(productIds);
        String shippingAddress = getRandomAddress();
        
        Order order = new Order.Builder()
                .id(orderId)
                .userId(userId)
                .productIds(productIds)
                .totalAmount(totalAmount)
                .status("CREATED")
                .shippingAddress(shippingAddress)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        
        logger.debug("Created test order: {}", order);
        return order;
    }
    
    /**
     * Converts a user entity to a user domain object
     * 
     * @param entity the user entity to convert
     * @return the user domain object
     */
    private User convertToUserDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new User.Builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                // Pass the password hash directly
                .build();
    }
    
    /**
     * Creates an in-memory user object (not persisted)
     * 
     * @param username the username for the user
     * @return the user domain object
     */
    private User createInMemoryUser(String username) {
        // Use random.nextInt() to generate a positive random number and convert to Long
        Long userId = (long) (random.nextInt(10000) + 1);
        return new User.Builder()
                .id(userId)
                .username(username)
                .email(username + "@test.com")
                .firstName("Test")
                .lastName("User")
                // Just set fields directly
                .active(true)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }
    
    /**
     * Generates a list of random product IDs for test orders
     * 
     * @return list of product IDs
     */
    private List<Long> generateRandomProductIds() {
        // In a real implementation, this might query the database for actual product IDs
        int numProducts = 1 + random.nextInt(3); // 1-3 products per order
        List<Long> productIds = new ArrayList<>();
        
        for (int i = 0; i < numProducts; i++) {
            // Generate product IDs in the range 1-100
            productIds.add(1L + random.nextInt(100));
        }
        
        return productIds;
    }
    
    /**
     * Calculates a total amount based on the product IDs
     * 
     * @param productIds the product IDs in the order
     * @return the calculated total amount
     */
    private BigDecimal calculateTotalAmount(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // In a real implementation, this would look up actual product prices
        // For testing, we'll just use a simple calculation
        BigDecimal basePrice = new BigDecimal("10.00");
        BigDecimal total = BigDecimal.ZERO;
        
        for (Long productId : productIds) {
            // Simple price calculation based on product ID
            BigDecimal price = basePrice.add(new BigDecimal(productId % 10));
            total = total.add(price);
        }
        
        return total;
    }
    
    /**
     * Returns a random shipping address from the default addresses
     * 
     * @return a random address string
     */
    private String getRandomAddress() {
        return DEFAULT_ADDRESSES[random.nextInt(DEFAULT_ADDRESSES.length)];
    }
    
    // TODO: Add methods to create more complex test data sets
    
    // TODO: Implement cleanup methods to remove test data after tests
    
    // FIXME: Current implementation doesn't handle database transaction boundaries properly
}