package com.gradle80.service.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handler for service layer exceptions.
 * Standardizes exception handling in the service layer.
 * 
 * This class:
 * - Logs all exceptions consistently
 * - Transforms some exceptions (e.g. not found) into more specific types
 * - Provides helper methods to simplify exception handling
 */
@Component
public class ServiceExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);
    
    /**
     * Handles entity not found scenarios.
     * 
     * @param entityName the name of the entity type
     * @param identifier the identifier that was not found
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleEntityNotFound(String entityName, Object identifier) {
        String message = String.format("%s with identifier %s not found", entityName, identifier);
        LOGGER.warn("Entity not found: {}", message);
        return new EntityNotFoundException(message);
    }
    
    /**
     * Handles validation failure scenarios.
     * 
     * @param message the validation failure message
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleValidationFailure(String message) {
        LOGGER.warn("Validation failure: {}", message);
        return new ValidationException(message);
    }
    
    /**
     * Handles business logic failure scenarios.
     * 
     * @param message the business rule violation message
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleBusinessRuleViolation(String message) {
        LOGGER.warn("Business rule violation: {}", message);
        return new BusinessRuleException(message);
    }
    
    /**
     * Handles security violation scenarios.
     * 
     * @param message the security violation message
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleSecurityViolation(String message) {
        LOGGER.warn("Security violation: {}", message);
        return new SecurityViolationException(message);
    }
    
    /**
     * Handles unexpected errors.
     * 
     * @param e the unexpected exception
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleUnexpectedError(Throwable e) {
        LOGGER.error("Unexpected error", e);
        return new ServiceException("An unexpected error occurred: " + e.getMessage(), e);
    }
    
    /**
     * Handles integration errors with external systems.
     * 
     * @param system the external system name
     * @param message the error details
     * @param e the underlying exception
     * @return RuntimeException a standardized exception
     */
    public RuntimeException handleIntegrationError(String system, String message, Throwable e) {
        LOGGER.error("Integration error with {}: {}", system, message, e);
        return new IntegrationException(
                String.format("Error communicating with %s: %s", system, message), e);
    }
}