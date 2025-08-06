package com.gradle80.common.exception;

/**
 * Interface for providing error response information.
 * <p>
 * This interface defines the contract for classes that provide error response
 * details, including error codes and messages that can be used for constructing
 * error responses to clients.
 * </p>
 */
public interface ErrorResponseProvider {

    /**
     * Returns the error code associated with this error response.
     * <p>
     * Error codes should be unique identifiers that can be used to categorize
     * and track different types of errors.
     * </p>
     *
     * @return a string representation of the error code
     */
    String getErrorCode();

    /**
     * Returns the error message associated with this error response.
     * <p>
     * The error message should provide a human-readable description of what
     * went wrong and potentially how to fix it.
     * </p>
     *
     * @return a string containing the error message
     */
    String getErrorMessage();
    
    // TODO: Consider adding methods for retrieving additional error details like timestamp or request ID
    
    // TODO: Consider adding method for retrieving HTTP status code if applicable
}