package com.gradle80.web.filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Interface for enhancing API responses.
 * 
 * Implementations of this interface add standard headers, metadata,
 * or other enhancements to API responses before they are returned to clients.
 * Used by controllers to standardize response format across the application.
 *
 * @author Gradle80 Web Team
 * @version 1.0
 */
public interface ResponseEnhancer {
    
    /**
     * Enhances a response by adding standard headers, metadata, or other information
     * before sending it to the client.
     *
     * @param <T> The type of the response body
     * @param body The response body object to be sent
     * @param status The HTTP status code for the response
     * @return An enhanced ResponseEntity containing the body and additional information
     * 
     * @see org.springframework.http.ResponseEntity
     * @see org.springframework.http.HttpStatus
     */
    <T> ResponseEntity<T> enhance(T body, HttpStatus status);
    
    // TODO: Consider adding methods for specialized response types (success, error, etc.)
    
    // FIXME: Evaluate if this interface should be extended to support streaming responses
}