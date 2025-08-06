package com.gradle80.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard error response format for API error handling.
 * This class represents a structured error response that provides
 * consistent error information across the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The timestamp when the error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * The error code, typically used for client-side error handling
     */
    private String code;
    
    /**
     * A human-readable error message describing the error
     */
    private String message;
    
    /**
     * The request path that resulted in the error
     */
    private String path;
    
    /**
     * Additional error details as key-value pairs
     */
    private Map<String, Object> details;
    
    /**
     * Creates a new builder instance for fluent ErrorResponse creation
     * 
     * @return a new ErrorResponseBuilder instance
     */
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }
    
    /**
     * Builder class for ErrorResponse objects.
     * Implements the builder pattern to allow for fluent, readable object creation.
     */
    public static class ErrorResponseBuilder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private String code;
        private String message;
        private String path;
        private Map<String, Object> details = new HashMap<>();
        
        /**
         * Private constructor - use ErrorResponse.builder() to create
         */
        private ErrorResponseBuilder() {
            // Use ErrorResponse.builder() to instantiate
        }
        
        /**
         * Sets the error timestamp
         * 
         * @param timestamp the error timestamp
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the error code
         * 
         * @param code the error code
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder code(String code) {
            this.code = code;
            return this;
        }
        
        /**
         * Sets the error message
         * 
         * @param message the error message
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        /**
         * Sets the request path
         * 
         * @param path the request path
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }
        
        /**
         * Sets the error details map
         * 
         * @param details the error details map
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }
        
        /**
         * Adds a single detail entry to the details map
         * 
         * @param key the detail key
         * @param value the detail value
         * @return this builder for method chaining
         */
        public ErrorResponseBuilder addDetail(String key, Object value) {
            if (this.details == null) {
                this.details = new HashMap<>();
            }
            this.details.put(key, value);
            return this;
        }
        
        /**
         * Builds the ErrorResponse instance with the configured values
         * 
         * @return a new ErrorResponse instance
         */
        public ErrorResponse build() {
            // TODO: Consider validating required fields before building
            return new ErrorResponse(timestamp, code, message, path, details);
        }
    }
}