package com.gradle80.web.filter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Standard implementation of {@link ResponseEnhancer} interface.
 * 
 * This class enhances API responses by adding standard headers and metadata
 * to maintain consistency across all API responses in the application.
 * It adds timestamps, request IDs, and other standard information to responses.
 *
 * @author Gradle80 Web Team
 * @version 1.0
 */
@Service
public class StandardResponseEnhancer implements ResponseEnhancer {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Enhances the response by adding standard headers and wrapping it in a 
     * ResponseEntity with appropriate status.
     *
     * @param <T> The type of the response body
     * @param body The response body object to be sent
     * @param status The HTTP status code for the response
     * @return An enhanced ResponseEntity containing the body and additional headers
     */
    @Override
    public <T> ResponseEntity<T> enhance(T body, HttpStatus status) {
        // Add standard headers to the response
        HttpHeaders headers = addStandardHeaders();
        
        // Return the enhanced response with body, headers, and status
        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Creates and populates HTTP headers with standard values that should be
     * included in all API responses.
     *
     * @return HttpHeaders object with standard headers populated
     */
    public HttpHeaders addStandardHeaders() {
        HttpHeaders headers = new HttpHeaders();
        
        // Add timestamp header for when the response was generated
        headers.add("X-Timestamp", TIMESTAMP_FORMATTER.format(ZonedDateTime.now()));
        
        // Add a unique request ID for tracking/debugging
        headers.add("X-Request-ID", generateRequestId());
        
        // Add API version information
        headers.add("X-API-Version", "1.0");
        
        // Add server information (can be configured based on environment)
        headers.add("X-Server-ID", getServerIdentifier());
        
        // TODO: Add additional security headers like Content-Security-Policy
        
        // FIXME: Consider adding rate limit information headers (remaining requests, reset time)
        
        return headers;
    }
    
    /**
     * Generates a unique request ID for tracking purposes.
     *
     * @return A string containing a unique identifier
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Returns the server identifier for the current instance.
     * This could be pulled from configuration in a production environment.
     *
     * @return A string containing the server identifier
     */
    private String getServerIdentifier() {
        // In a real production environment, this would be configurable
        // and would provide an identifier for the specific server instance
        return "web-service-default";
    }
}