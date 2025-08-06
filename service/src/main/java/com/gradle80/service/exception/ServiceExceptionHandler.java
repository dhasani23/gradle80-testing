package com.gradle80.service.exception;

import com.gradle80.common.dto.ErrorResponse;
import com.gradle80.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for service-layer exceptions.
 * 
 * This class centrally handles exceptions that occur in the service layer,
 * translating them into appropriate ErrorResponse objects with standardized format.
 */
@RestControllerAdvice
public class ServiceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceExceptionHandler.class);
    
    /**
     * Handles EntityNotFoundException, which is thrown when an entity could not be found.
     * 
     * @param ex the exception
     * @param request the current web request
     * @return ErrorResponse with appropriate error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.error("Entity not found exception: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", System.currentTimeMillis());
        details.put("status", HttpStatus.NOT_FOUND.value());
        
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getDescription(false).substring(4))  // Remove "uri=" prefix
                .details(details)
                .build();
    }
    
    /**
     * Handles AuthenticationException, which is thrown during authentication failures.
     * 
     * @param ex the exception
     * @param request the current web request
     * @return ErrorResponse with appropriate error details
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(org.springframework.security.core.AuthenticationException ex, WebRequest request) {
        logger.error("Authentication exception: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", System.currentTimeMillis());
        details.put("status", HttpStatus.UNAUTHORIZED.value());
        
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("AUTHENTICATION_FAILED")
                .message(ex.getMessage())
                .path(request.getDescription(false).substring(4))  // Remove "uri=" prefix
                .details(details)
                .build();
    }
    
    /**
     * Handles ValidationException, which is thrown when data validation fails.
     * 
     * @param ex the exception
     * @param request the current web request
     * @return ErrorResponse with appropriate error details
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation exception: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", System.currentTimeMillis());
        details.put("status", HttpStatus.BAD_REQUEST.value());
        
        // Extract additional validation details if available
        if (ex.getCause() != null) {
            details.put("cause", ex.getCause().getMessage());
        }
        
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("VALIDATION_FAILED")
                .message(ex.getMessage())
                .path(request.getDescription(false).substring(4))  // Remove "uri=" prefix
                .details(details)
                .build();
    }
    
    /**
     * Fallback handler for all other exceptions not explicitly handled elsewhere.
     * 
     * @param ex the exception
     * @param request the current web request
     * @return ErrorResponse with appropriate error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred", ex);
        
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", System.currentTimeMillis());
        details.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        // Don't include stack trace or technical details in response
        
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please contact support if the problem persists.")
                .path(request.getDescription(false).substring(4))  // Remove "uri=" prefix
                .details(details)
                .build();
    }
    
    // FIXME: Consider implementing a more comprehensive error code mapping system
    
    // TODO: Add handling for additional exception types like DataAccessException, 
    // AccessDeniedException, etc.
}