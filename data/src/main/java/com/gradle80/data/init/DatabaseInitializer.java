package com.gradle80.data.init;

import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.ProductRepository;
import com.gradle80.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Database initialization component.
 * 
 * This component is responsible for initializing the database with default users
 * and sample products when the application starts. It only runs in development
 * and test profiles to avoid modifying production data.
 */
@Component
@Profile({"dev", "test"}) // Only run in development and test profiles
public class DatabaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor with required dependencies.
     * 
     * @param userRepository repository for user operations
     * @param productRepository repository for product operations
     */
    @Autowired
    public DatabaseInitializer(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Entry point for database initialization, called when the application starts.
     * 
     * @param args command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        logger.info("Starting database initialization...");
        initDatabase();
        logger.info("Database initialization completed");
    }
    
    /**
     * Initialize data in the database.
     * Creates default users and sample products if they don't exist.
     */
    @Transactional
    public void initDatabase() {
        // Check if initialization is needed
        if (isDatabaseEmpty()) {
            logger.info("Database is empty, populating with initial data");
            createDefaultUsers();
            createSampleProducts();
        } else {
            logger.info("Database already contains data, skipping initialization");
        }
    }
    
    /**
     * Create default users in the database.
     * These users provide basic access to the system for testing and development.
     */
    @Transactional
    public void createDefaultUsers() {
        logger.info("Creating default users");
        
        // Create admin user if it doesn't exist
        if (userRepository.findByUsername("admin") == null) {
            UserEntity adminUser = new UserEntity(
                "admin",
                "admin@gradle80.com",
                "System",
                "Administrator",
                "$2a$10$dL4JR.udkWrspmO5bY0.3.QnD7pHIgfsVZ.UcYxpwxlz9.JMWB3.u" // hashed password: admin123
            );
            userRepository.save(adminUser);
            logger.info("Created admin user: {}", adminUser.getUsername());
        }
        
        // Create test users
        List<UserEntity> testUsers = Arrays.asList(
            new UserEntity(
                "user1",
                "user1@example.com",
                "John",
                "Doe",
                "$2a$10$7bJKQs1p6fWGIhMbgJQiEe6F8R5MBGB5VL1kk87LS6N2S9ZNM9wHi" // hashed password: password1
            ),
            new UserEntity(
                "user2",
                "user2@example.com",
                "Jane",
                "Smith",
                "$2a$10$7bJKQs1p6fWGIhMbgJQiEe6F8R5MBGB5VL1kk87LS6N2S9ZNM9wHi" // hashed password: password1
            ),
            new UserEntity(
                "user3",
                "user3@example.com",
                "Robert",
                "Johnson",
                "$2a$10$7bJKQs1p6fWGIhMbgJQiEe6F8R5MBGB5VL1kk87LS6N2S9ZNM9wHi" // hashed password: password1
            )
        );
        
        // Save all test users that don't exist yet
        for (UserEntity user : testUsers) {
            if (userRepository.findByUsername(user.getUsername()) == null) {
                userRepository.save(user);
                logger.info("Created test user: {}", user.getUsername());
            }
        }
        
        // TODO: Implement role assignment once role management is added to the system
        
        logger.info("Default users created successfully");
    }
    
    /**
     * Create sample products in the database.
     * These products provide example data for testing and development.
     */
    @Transactional
    public void createSampleProducts() {
        logger.info("Creating sample products");
        
        // Skip if we already have products
        if (productRepository.count() > 0) {
            logger.info("Products already exist, skipping sample product creation");
            return;
        }
        
        // Create products by category
        
        // Electronics category
        List<ProductEntity> electronics = Arrays.asList(
            new ProductEntity(
                "Smartphone X1",
                "Latest model with advanced features and high-resolution display",
                new BigDecimal("799.99"),
                "Electronics",
                true
            ),
            new ProductEntity(
                "Laptop Pro",
                "Professional laptop with high performance for developers",
                new BigDecimal("1299.99"),
                "Electronics",
                true
            ),
            new ProductEntity(
                "Wireless Headphones",
                "Noise cancelling with premium sound quality",
                new BigDecimal("199.99"),
                "Electronics",
                true
            )
        );
        
        // Home & Kitchen category
        List<ProductEntity> homeKitchen = Arrays.asList(
            new ProductEntity(
                "Coffee Maker",
                "Programmable coffee maker with timer",
                new BigDecimal("89.99"),
                "Home & Kitchen",
                true
            ),
            new ProductEntity(
                "Blender",
                "High-powered blender for smoothies and food preparation",
                new BigDecimal("79.99"),
                "Home & Kitchen",
                true
            ),
            new ProductEntity(
                "Toaster Oven",
                "6-slice convection toaster oven with digital controls",
                new BigDecimal("129.99"),
                "Home & Kitchen",
                false // Temporarily out of stock
            )
        );
        
        // Books category
        List<ProductEntity> books = Arrays.asList(
            new ProductEntity(
                "Java Programming Guide",
                "Comprehensive guide to Java programming for all levels",
                new BigDecimal("49.99"),
                "Books",
                true
            ),
            new ProductEntity(
                "Spring Boot in Action",
                "Learn Spring Boot development with practical examples",
                new BigDecimal("39.99"),
                "Books",
                true
            ),
            new ProductEntity(
                "Design Patterns",
                "Essential guide to software design patterns",
                new BigDecimal("45.99"),
                "Books",
                true
            )
        );
        
        // Save all product categories - use saveAll instead of save for lists
        productRepository.saveAll(electronics);
        productRepository.saveAll(homeKitchen);
        productRepository.saveAll(books);
        
        logger.info("Created {} sample products", electronics.size() + homeKitchen.size() + books.size());
        
        // FIXME: Product images are not being set - implement image handling in the future
    }
    
    /**
     * Check if the database is empty and needs initialization.
     * 
     * @return true if the database is empty (no users or products)
     */
    private boolean isDatabaseEmpty() {
        return userRepository.count() == 0 || productRepository.count() == 0;
    }
}