package com.gradle80.common.validation;

/**
 * Exception thrown when validation fails.
 * This exception is intended to be used by implementations of the {@link Validator} interface
 * to indicate validation failures that aren't covered by standard exceptions.
 */
public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new validation exception with null as its detail message.
     */
    public ValidationException() {
        super();
    }
    
    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new validation exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new validation exception with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
}