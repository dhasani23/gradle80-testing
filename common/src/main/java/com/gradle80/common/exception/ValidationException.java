package com.gradle80.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when validation fails.
 * 
 * This exception is used by validation utilities to indicate that
 * a validation check has failed.
 */
public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * List of validation error messages
     */
    private final List<String> errors = new ArrayList<>();
    
    /**
     * Error code for this validation failure
     */
    private String errorCode = "VALIDATION_ERROR";
    
    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
        this.errors.add(message);
    }
    
    /**
     * Constructs a new validation exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors.add(message);
    }
    
    /**
     * Constructs a new validation exception with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new validation exception with the specified error list.
     *
     * @param errors list of validation error messages
     */
    public ValidationException(List<String> errors) {
        super(errors != null && !errors.isEmpty() ? errors.get(0) : "Validation failed");
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }
    
    /**
     * Constructs a new validation exception with the specified error code and error list.
     *
     * @param errorCode the error code
     * @param errors list of validation error messages
     */
    public ValidationException(String errorCode, List<String> errors) {
        this(errors);
        this.errorCode = errorCode;
    }
    
    /**
     * Get the list of validation errors.
     *
     * @return list of error messages
     */
    public List<String> getErrors() {
        return this.errors;
    }
    
    /**
     * Get the error code for this validation failure.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return this.errorCode;
    }
    
    /**
     * Add an error message to the list of errors.
     *
     * @param errorMessage the error message to add
     */
    public void addError(String errorMessage) {
        this.errors.add(errorMessage);
    }
    
    /**
     * Set the error code.
     *
     * @param errorCode the error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}