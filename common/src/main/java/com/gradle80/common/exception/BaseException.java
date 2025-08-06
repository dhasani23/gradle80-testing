package com.gradle80.common.exception;

/**
 * BaseException serves as the base exception class for all application exceptions.
 * It provides additional functionality for error code management.
 * 
 * This class extends RuntimeException to allow for unchecked exceptions.
 */
public class BaseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The error code associated with this exception.
     * This can be used for client-side error handling and internationalization.
     */
    private final String errorCode;
    
    /**
     * Constructs a new BaseException with the specified error code.
     * 
     * @param errorCode the error code for this exception
     */
    public BaseException(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new BaseException with the specified error code and detail message.
     * 
     * @param errorCode the error code for this exception
     * @param message the detail message
     */
    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new BaseException with the specified error code, detail message,
     * and cause.
     * 
     * @param errorCode the error code for this exception
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new BaseException with the specified error code and cause.
     * 
     * @param errorCode the error code for this exception
     * @param cause the cause of this exception
     */
    public BaseException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the error code associated with this exception.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Returns a string representation of this exception, including the error code.
     * 
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        return "BaseException [errorCode=" + errorCode + "] " + super.toString();
    }
}