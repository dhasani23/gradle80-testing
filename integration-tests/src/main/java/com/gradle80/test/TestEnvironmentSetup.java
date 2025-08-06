package com.gradle80.test;

/**
 * Interface for handling test environment configuration and teardown.
 * Implementations of this interface should manage resources needed for integration tests.
 */
public interface TestEnvironmentSetup {
    
    /**
     * Initializes the test environment before test execution.
     * This may include starting services, setting up mocks, or preparing test data.
     */
    void initializeEnvironment();
    
    /**
     * Cleans up the test environment after test execution.
     * This may include stopping services, cleaning databases, or releasing resources.
     */
    void cleanupEnvironment();
    
    /**
     * Gets a named property from the test environment.
     * 
     * @param propertyName the name of the property to retrieve
     * @return the property value or null if not found
     */
    String getProperty(String propertyName);
    
    /**
     * Sets a named property in the test environment.
     * 
     * @param propertyName the name of the property to set
     * @param value the value to set
     */
    void setProperty(String propertyName, String value);
}