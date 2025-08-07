package com.gradle80.web.exception;

import com.gradle80.common.dto.ErrorResponse;
import com.gradle80.common.exception.ResourceNotFoundException;
import com.gradle80.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global web exception handler for centralized API error handling.
 * This class provides consistent error responses across the application
 * by intercepting exceptions and converting them to standardized ErrorResponse objects.
 */
@ControllerAdvice
public class WebExceptionHandler {
    
    /**
     * Logger instance for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);
    
    /**
     * Handles EntityNotFoundException and ResourceNotFoundException instances.
     * Returns a 404 NOT FOUND response with appropriate error details.
     *
     * @param ex The not found exception
     * @param request The web request
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Exception ex, WebRequest request) {
        logger.error("Resource not found exception: {}", ex.getMessage());
        
        String path = extractPath(request);
        String errorCode = "RESOURCE_NOT_FOUND";
        
        // Handle specific code for ResourceNotFoundException
        if (ex instanceof ResourceNotFoundException) {
            errorCode = ((ResourceNotFoundException) ex).getErrorCode();
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(errorCode)
                .message(ex.getMessage())
                .path(path)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles authentication exceptions.
     * Returns a 401 UNAUTHORIZED response with appropriate error details.
     *
     * @param ex The authentication exception
     * @param request The web request
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("Authentication exception: {}", ex.getMessage());
        
        String path = extractPath(request);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("AUTHENTICATION_ERROR")
                .message("Authentication failed: " + ex.getMessage())
                .path(path)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles validation exceptions.
     * Returns a 400 BAD REQUEST response with validation error details.
     *
     * @param ex The validation exception
     * @param request The web request
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation exception: {}", ex.getMessage());
        
        String path = extractPath(request);
        Map<String, Object> details = new HashMap<>();
        
        // Add validation errors to details
        if (ex.getErrors() != null) {
            details.put("validationErrors", ex.getErrors());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(ex.getErrorCode())
                .message("Validation failed: " + ex.getMessage())
                .path(path)
                .details(details)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Catch-all handler for any unhandled exceptions.
     * Returns a 500 INTERNAL SERVER ERROR response.
     *
     * @param ex The exception
     * @param request The web request
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        // Log the full stack trace for unexpected errors
        logger.error("Unhandled exception occurred", ex);
        
        String path = extractPath(request);
        
        // FIXME: In a production environment, avoid exposing detailed error messages to clients
        // Consider using a more generic message instead of ex.getMessage()
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .path(path)
                .build();
        
        // TODO: Implement notification system for critical unhandled exceptions
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Extracts the request path from the WebRequest object.
     *
     * @param request The web request
     * @return The request path or "unknown" if it cannot be determined
     */
    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "unknown";
    }
}