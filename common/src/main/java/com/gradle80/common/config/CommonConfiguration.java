package com.gradle80.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Common module Spring configuration.
 * <p>
 * This class provides configuration beans that are shared across the application,
 * including JSON serialization configuration and application properties.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class CommonConfiguration {

    /**
     * Configures and provides the ObjectMapper bean for JSON serialization/deserialization.
     * <p>
     * The configured ObjectMapper includes:
     * <ul>
     *   <li>Java 8 date/time module support</li>
     *   <li>Indented output for better readability</li>
     *   <li>ISO-8601 date format instead of timestamps</li>
     *   <li>Exclusion of null values from serialized output</li>
     * </ul>
     * </p>
     *
     * @return the configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register Java 8 datetime module
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configure serialization features
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        
        // Exclude null fields from JSON output
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // TODO: Add custom serializers/deserializers for project-specific types if needed
        
        return objectMapper;
    }

    /**
     * Provides the application properties bean.
     * <p>
     * This bean is automatically populated from properties with the "application" prefix
     * in configuration files.
     * </p>
     *
     * @return the application properties instance
     */
    @Bean
    public ApplicationProperties applicationProperties() {
        // This bean is created automatically by Spring Boot through @EnableConfigurationProperties
        // We're just exposing it explicitly as a bean
        return new ApplicationProperties();
    }
    
    // FIXME: Consider adding health check indicators for critical services
}