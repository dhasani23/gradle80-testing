package com.gradle80.test.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gradle80.test.TestEnvironmentSetup;

/**
 * Implementation of test environment setup and teardown functionality.
 * 
 * This class handles the initialization and cleanup of resources needed for integration
 * tests, such as loading test properties, preparing the test environment, and releasing
 * resources after test completion.
 */
@Component
public class TestEnvironmentSetupImpl implements TestEnvironmentSetup {
    
    private static final Logger logger = LoggerFactory.getLogger(TestEnvironmentSetupImpl.class);
    
    /**
     * Test properties loaded from configuration files.
     */
    private Properties testProperties;
    
    @Value("${test.properties.file:classpath:test.properties}")
    private String testPropertiesFile;
    
    @Value("${test.environment.cleanup.enabled:true}")
    private boolean cleanupEnabled;
    
    /**
     * Initialize the test environment setup upon bean creation.
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing TestEnvironmentSetupImpl");
        testProperties = new Properties();
        
        try {
            // Load default test properties
            loadDefaultProperties();
        } catch (IOException e) {
            logger.warn("Failed to load default test properties", e);
        }
    }
    
    /**
     * Load default properties from the configured properties file.
     * 
     * @throws IOException if there's an error loading properties
     */
    private void loadDefaultProperties() throws IOException {
        String resolvedPath = testPropertiesFile;
        
        if (testPropertiesFile.startsWith("classpath:")) {
            resolvedPath = getClass().getClassLoader()
                .getResource(testPropertiesFile.substring("classpath:".length()))
                .getPath();
        }
        
        File propertiesFile = new File(resolvedPath);
        if (propertiesFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propertiesFile)) {
                testProperties.load(fis);
                logger.debug("Loaded {} properties from {}", testProperties.size(), resolvedPath);
            }
        } else {
            logger.warn("Test properties file not found: {}", resolvedPath);
        }
    }

    /**
     * Initializes the environment before test execution.
     * This includes setting up necessary resources, test data, and configurations.
     */
    @Override
    public void initializeEnvironment() {
        logger.info("Initializing test environment");
        
        try {
            // Set up system properties needed for tests
            setupSystemProperties();
            
            // Initialize any external services or resources
            initializeExternalResources();
            
            // Validate that the environment is properly set up
            validateEnvironment();
            
            logger.info("Test environment initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize test environment", e);
            throw new RuntimeException("Test environment initialization failed", e);
        }
    }

    /**
     * Sets up system properties needed for testing.
     */
    private void setupSystemProperties() {
        logger.debug("Setting up system properties for testing");
        
        // Copy test properties to system properties if they have the prefix "system."
        testProperties.stringPropertyNames().stream()
            .filter(name -> name.startsWith("system."))
            .forEach(name -> {
                String sysPropName = name.substring("system.".length());
                System.setProperty(sysPropName, testProperties.getProperty(name));
                logger.trace("Set system property: {}={}", sysPropName, testProperties.getProperty(name));
            });
    }
    
    /**
     * Initialize any external resources needed for testing.
     * 
     * TODO: Add specific external resource initialization as needed (databases, servers, etc.)
     */
    private void initializeExternalResources() {
        logger.debug("Initializing external resources");
        
        // This is where you would initialize test databases, mock servers, etc.
    }
    
    /**
     * Validates that the test environment is properly set up.
     * 
     * @throws IllegalStateException if the environment is not valid
     */
    private void validateEnvironment() {
        logger.debug("Validating test environment");
        
        // Check for required properties
        String[] requiredProps = {"test.db.url", "test.db.username"};
        for (String prop : requiredProps) {
            if (testProperties.getProperty(prop) == null) {
                logger.warn("Missing required property: {}", prop);
                // FIXME: Decide whether to fail tests or just log warnings for missing properties
            }
        }
    }

    /**
     * Cleans up the environment after test execution.
     * This includes releasing resources, resetting configurations, and cleaning up test data.
     */
    @Override
    public void cleanupEnvironment() {
        logger.info("Cleaning up test environment");
        
        if (!cleanupEnabled) {
            logger.info("Test environment cleanup is disabled");
            return;
        }
        
        try {
            // Clean up any external resources
            cleanupExternalResources();
            
            // Reset system properties
            resetSystemProperties();
            
            logger.info("Test environment cleaned up successfully");
        } catch (Exception e) {
            logger.error("Failed to clean up test environment", e);
            // We log but don't rethrow to ensure cleanup completes as much as possible
        }
    }
    
    /**
     * Cleans up any external resources used in tests.
     * 
     * TODO: Add specific external resource cleanup as needed
     */
    private void cleanupExternalResources() {
        logger.debug("Cleaning up external resources");
        
        // This is where you would clean up test databases, stop mock servers, etc.
    }
    
    /**
     * Resets system properties that were set during initialization.
     */
    private void resetSystemProperties() {
        logger.debug("Resetting system properties");
        
        testProperties.stringPropertyNames().stream()
            .filter(name -> name.startsWith("system."))
            .forEach(name -> {
                String sysPropName = name.substring("system.".length());
                System.clearProperty(sysPropName);
                logger.trace("Cleared system property: {}", sysPropName);
            });
    }

    /**
     * Gets a property from the test environment.
     * 
     * @param propertyName the name of the property to retrieve
     * @return the property value or null if not found
     */
    @Override
    public String getProperty(String propertyName) {
        return testProperties.getProperty(propertyName);
    }

    /**
     * Sets a property in the test environment.
     * 
     * @param propertyName the name of the property to set
     * @param value the value to set
     */
    @Override
    public void setProperty(String propertyName, String value) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Property name cannot be null");
        }
        
        logger.trace("Setting property: {}={}", propertyName, value);
        testProperties.setProperty(propertyName, value);
        
        // Also set as system property if it has the system prefix
        if (propertyName.startsWith("system.")) {
            String sysPropName = propertyName.substring("system.".length());
            System.setProperty(sysPropName, value);
            logger.trace("Set system property: {}={}", sysPropName, value);
        }
    }
    
    /**
     * Cleanup resources when the bean is destroyed.
     */
    @PreDestroy
    public void destroy() {
        logger.info("Destroying TestEnvironmentSetupImpl");
        cleanupEnvironment();
    }
}