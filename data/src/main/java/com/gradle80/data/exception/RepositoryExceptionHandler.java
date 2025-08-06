package com.gradle80.data.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Repository exception handler component that transforms specific JPA and database exceptions
 * into more meaningful application-specific exceptions.
 * <p>
 * This component centralizes the exception handling logic for data access operations
 * to ensure consistent error reporting and handling throughout the application.
 * </p>
 *
 * @author Gradle80 Team
 * @since 1.0.0
 */
@Component
public class RepositoryExceptionHandler {

    /**
     * Handles data integrity violation exceptions such as constraint violations,
     * unique key violations, or foreign key violations.
     * 
     * @param ex The data integrity violation exception caught during a repository operation
     * @return A more specific runtime exception with a meaningful message
     */
    public RuntimeException handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // Extract the root cause to provide more meaningful information
        Throwable rootCause = getRootCause(ex);
        String message = rootCause.getMessage();
        
        // Check for specific types of constraint violations
        if (message.contains("unique constraint") || message.contains("Duplicate entry")) {
            return new DataModuleException("Duplicate data found. The data you are trying to save violates a uniqueness constraint.", "DATA_DUPLICATE_KEY", ex);
        } else if (message.contains("foreign key constraint") || message.contains("referential integrity")) {
            return new DataModuleException("Referenced data does not exist or cannot be modified due to existing references.", "DATA_REFERENTIAL_INTEGRITY", ex);
        } else if (message.contains("check constraint")) {
            return new DataModuleException("Data validation failed. The provided data does not meet the required constraints.", "DATA_CHECK_CONSTRAINT", ex);
        }
        
        // Default case for other integrity violations
        return new DataModuleException("Data integrity violation occurred during database operation.", "DATA_INTEGRITY_ERROR", ex);
    }

    /**
     * Handles entity not found exceptions which occur when attempting to retrieve
     * an entity that doesn't exist in the database.
     * 
     * @param ex The entity not found exception caught during a repository operation
     * @return A more specific runtime exception with a meaningful message
     */
    public RuntimeException handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = ex.getMessage();
        String entityType = extractEntityType(message);
        String entityId = extractEntityId(message);
        
        // Create a specific exception with detailed information
        return new DataModuleException(
            String.format("The requested %s with ID %s was not found.", entityType, entityId),
            "ENTITY_NOT_FOUND",
            ex
        );
    }
    
    /**
     * Gets the root cause of an exception by traversing the cause chain.
     * 
     * @param throwable The exception to find the root cause for
     * @return The root cause exception
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return throwable;
        }
        return getRootCause(cause);
    }
    
    /**
     * Extracts the entity type from an entity not found exception message.
     * 
     * @param message The exception message
     * @return The extracted entity type or "entity" if not determinable
     */
    private String extractEntityType(String message) {
        // TODO: Implement more robust entity type extraction based on message patterns
        // This is a simple implementation that should be enhanced based on actual message formats
        if (message == null) {
            return "entity";
        }
        
        // Try to extract entity type from common patterns
        for (String entityType : new String[]{"User", "Product", "Order", "OrderItem", "Notification"}) {
            if (message.contains(entityType)) {
                return entityType;
            }
        }
        
        return "entity";
    }
    
    /**
     * Extracts the entity ID from an entity not found exception message.
     * 
     * @param message The exception message
     * @return The extracted entity ID or "unknown" if not determinable
     */
    private String extractEntityId(String message) {
        // TODO: Implement more robust ID extraction using regex pattern matching
        // FIXME: Current implementation is naive and might not work for all message formats
        if (message == null) {
            return "unknown";
        }
        
        // Try to extract ID using common patterns
        String[] parts = message.split("id");
        if (parts.length > 1) {
            String idPart = parts[1].trim();
            // Extract the numeric part
            StringBuilder id = new StringBuilder();
            for (char c : idPart.toCharArray()) {
                if (Character.isDigit(c)) {
                    id.append(c);
                } else if (id.length() > 0) {
                    break;
                }
            }
            if (id.length() > 0) {
                return id.toString();
            }
        }
        
        return "unknown";
    }
}