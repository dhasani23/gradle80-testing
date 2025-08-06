package com.gradle80.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application-wide properties and settings.
 * <p>
 * This class holds configuration properties for the application that can be
 * set via application.properties or application.yml files.
 * </p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    
    /**
     * The name of the application
     */
    private String appName;
    
    /**
     * The version of the application
     */
    private String version;
    
    /**
     * The runtime environment (e.g., dev, test, prod)
     */
    private String environment;
    
    /**
     * Default page size for paginated results
     */
    private int defaultPageSize;
    
    /**
     * Returns a string representation of the application properties
     * @return a string containing the main application properties
     */
    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "appName='" + appName + '\'' +
                ", version='" + version + '\'' +
                ", environment='" + environment + '\'' +
                ", defaultPageSize=" + defaultPageSize +
                '}';
    }
    
    /**
     * Checks if the current environment is production
     * 
     * @return true if the environment is set to "prod" or "production"
     */
    public boolean isProduction() {
        return "prod".equalsIgnoreCase(environment) || 
               "production".equalsIgnoreCase(environment);
    }
    
    /**
     * Checks if the current environment is development
     * 
     * @return true if the environment is set to "dev" or "development"
     */
    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(environment) || 
               "development".equalsIgnoreCase(environment);
    }
    
    /**
     * Checks if the current environment is test
     * 
     * @return true if the environment is set to "test" or "testing"
     */
    public boolean isTest() {
        return "test".equalsIgnoreCase(environment) || 
               "testing".equalsIgnoreCase(environment);
    }
}