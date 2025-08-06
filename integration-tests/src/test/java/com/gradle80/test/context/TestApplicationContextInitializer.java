package com.gradle80.test.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application context initializer for tests.
 * 
 * This class initializes the Spring application context for test environments by:
 * 1. Setting up appropriate test profiles
 * 2. Loading test-specific property sources
 * 3. Setting dynamic properties based on test execution environment
 */
public class TestApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private static final Logger LOGGER = Logger.getLogger(TestApplicationContextInitializer.class.getName());
    
    private static final String TEST_PROPERTIES_LOCATION = "application-test.yml";
    private static final String TEST_PROFILE = "test";
    
    /**
     * Initialize the application context for tests
     *
     * @param context The configurable application context to be initialized
     */
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        LOGGER.info("Initializing test application context...");
        
        ConfigurableEnvironment environment = context.getEnvironment();
        
        // Ensure test profile is active
        setupTestProfile(environment);
        
        // Load test properties
        loadTestProperties(environment);
        
        // Set dynamic test properties
        setDynamicTestProperties(environment);
        
        LOGGER.info("Test application context initialization completed");
    }
    
    /**
     * Ensures the test profile is active in the Spring environment
     */
    private void setupTestProfile(ConfigurableEnvironment environment) {
        // Check if test profile is already active
        boolean hasTestProfile = false;
        for (String profile : environment.getActiveProfiles()) {
            if (TEST_PROFILE.equals(profile)) {
                hasTestProfile = true;
                break;
            }
        }
        
        // Add test profile if not already active
        if (!hasTestProfile) {
            LOGGER.info("Activating test profile");
            environment.addActiveProfile(TEST_PROFILE);
        }
    }
    
    /**
     * Loads test-specific properties from application-test.yml
     */
    private void loadTestProperties(ConfigurableEnvironment environment) {
        try {
            Resource testPropertiesResource = new ClassPathResource(TEST_PROPERTIES_LOCATION);
            
            if (testPropertiesResource.exists()) {
                LOGGER.info("Loading test properties from: " + TEST_PROPERTIES_LOCATION);
                environment.getPropertySources().addFirst(
                    new ResourcePropertySource(testPropertiesResource)
                );
            } else {
                LOGGER.warning("Test properties resource not found: " + TEST_PROPERTIES_LOCATION);
            }
        } catch (IOException e) {
            // Log error but continue - application should still work with defaults
            LOGGER.log(Level.WARNING, "Failed to load test properties", e);
        }
    }
    
    /**
     * Sets dynamic properties that may change between test runs
     */
    private void setDynamicTestProperties(ConfigurableEnvironment environment) {
        Map<String, Object> dynamicProps = new HashMap<>();
        
        // Set random port for embedded server to avoid conflicts in test runs
        dynamicProps.put("server.port", String.valueOf(getRandomPort()));
        
        // Set in-memory database configuration
        dynamicProps.put("spring.datasource.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dynamicProps.put("spring.datasource.username", "sa");
        dynamicProps.put("spring.datasource.password", "");
        
        // Set test-specific application properties
        dynamicProps.put("app.testing.enabled", "true");
        dynamicProps.put("app.testing.timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Add dynamic properties to environment
        MapPropertySource dynamicPropertySource = new MapPropertySource("dynamicTestProperties", dynamicProps);
        environment.getPropertySources().addFirst(dynamicPropertySource);
        
        LOGGER.info("Dynamic test properties configured");
    }
    
    /**
     * Generate a random port number to avoid conflicts in tests
     * 
     * @return A random port number between 10000 and 60000
     */
    private int getRandomPort() {
        // TODO: Consider using SocketUtils.findAvailableTcpPort() from Spring instead
        return 10000 + (int)(Math.random() * 50000);
    }
}