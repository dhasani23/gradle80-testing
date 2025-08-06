package com.gradle80.data.exception;

/**
 * Custom exception class for the data module.
 * 
 * This exception is used to encapsulate data-related errors with specific error codes
 * and provides factory methods for common error scenarios.
 * 
 * @author Gradle80 Development Team
 */
public class DataModuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Error code identifying the type of exception.
     */
    private final String errorCode;
    
    /**
     * Constructs a new DataModuleException with the specified error message.
     *
     * @param message the detail message
     */
    public DataModuleException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }
    
    /**
     * Constructs a new DataModuleException with the specified error message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public DataModuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new DataModuleException with the specified error message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause of the exception
     */
    public DataModuleException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Factory method that creates an exception for when an entity is not found.
     *
     * @param entityType the type of entity that was not found
     * @param id the ID of the entity that was not found
     * @return a new DataModuleException with appropriate message and error code
     */
    public static DataModuleException forEntityNotFound(String entityType, Long id) {
        String message = String.format("%s with ID %d not found", entityType, id);
        return new DataModuleException(message, "ENTITY_NOT_FOUND");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s [errorCode=%s]: %s", 
                getClass().getSimpleName(), errorCode, getMessage());
    }
    
    // TODO: Add additional factory methods for other common data access exceptions
    
    // FIXME: Consider adding internationalization support for error messages
}