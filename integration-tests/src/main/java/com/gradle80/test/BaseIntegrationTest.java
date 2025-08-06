package com.gradle80.test;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.test.config.TestConfig;

/**
 * Base class for all integration tests with common setup and utilities.
 * Provides standardized test initialization and cleanup procedures.
 * 
 * Extend this class to create specific integration tests with access to
 * common test environment, configuration, and utilities.
 */
public abstract class BaseIntegrationTest extends AbstractJUnit4SpringContextTests {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    
    /**
     * Test environment setup for initialization and cleanup
     */
    @Autowired
    protected TestEnvironmentSetup testEnvironment;
    
    /**
     * JSON object mapper for serialization and deserialization in tests
     */
    @Autowired
    protected ObjectMapper objectMapper;
    
    /**
     * Initializes the test environment before each test execution.
     * This method is automatically called before each test method.
     */
    @Before
    public void setUp() {
        logger.info("Setting up integration test environment");
        
        try {
            // Initialize the test environment
            testEnvironment.initializeEnvironment();
            
            // Perform additional setup specific to this test class
            doSetUp();
            
            logger.debug("Test environment setup completed");
        } catch (Exception e) {
            logger.error("Failed to set up test environment", e);
            // FIXME: Better error handling strategy needed here
            throw new RuntimeException("Test environment setup failed", e);
        }
    }
    
    /**
     * Hook method for subclasses to implement their specific setup logic.
     * This method is called at the end of the standard setUp process.
     */
    protected void doSetUp() {
        // Default implementation does nothing
        // Subclasses should override this method to add custom setup logic
    }
    
    /**
     * Cleans up the test environment after each test execution.
     * This method is automatically called after each test method.
     */
    @After
    public void tearDown() {
        logger.info("Tearing down integration test environment");
        
        try {
            // Perform specific teardown for this test class
            doTearDown();
            
            // Clean up the test environment
            testEnvironment.cleanupEnvironment();
            
            logger.debug("Test environment teardown completed");
        } catch (Exception e) {
            logger.error("Failed to tear down test environment", e);
            // We don't throw here to ensure teardown always completes
            // TODO: Consider adding a test failure notification mechanism
        }
    }
    
    /**
     * Hook method for subclasses to implement their specific teardown logic.
     * This method is called at the beginning of the standard tearDown process.
     */
    protected void doTearDown() {
        // Default implementation does nothing
        // Subclasses should override this method to add custom teardown logic
    }
    
    /**
     * Helper method to convert an object to its JSON representation.
     * 
     * @param object the object to convert to JSON
     * @return JSON string representation
     * @throws Exception if serialization fails
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Helper method to parse JSON into an object.
     * 
     * @param <T> the type of the target object
     * @param json the JSON string to parse
     * @param clazz the class of the target object
     * @return the parsed object
     * @throws Exception if deserialization fails
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}