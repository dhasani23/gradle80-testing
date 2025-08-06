package com.gradle80.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper.
 * This class provides a standardized format for API responses throughout the application.
 * 
 * @param <T> The type of data payload contained in this response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Indicates whether the request was successful.
     */
    private boolean success;
    
    /**
     * Response message. Contains error message in case of failure or
     * success message in case of successful operation.
     */
    private String message;
    
    /**
     * Response payload. Contains the actual data returned by the API.
     * May be null in case of errors.
     */
    private T data;
    
    /**
     * Timestamp when the response was generated.
     */
    private LocalDateTime timestamp;
    
    /**
     * Creates a successful API response with the provided data.
     *
     * @param data The data payload to be returned
     * @param <T> The type of the data payload
     * @return A new ApiResponse instance with success=true and the provided data
     */
    public static <T> ApiResponse<T> ofSuccess(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data, LocalDateTime.now());
    }
    
    /**
     * Creates an error API response with the provided error message.
     *
     * @param message The error message
     * @param <T> The type parameter for the response
     * @return A new ApiResponse instance with success=false and the provided error message
     */
    public static <T> ApiResponse<T> ofError(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
    
    /**
     * Creates an error API response with a default error message.
     * 
     * @param <T> The type parameter for the response
     * @return A new ApiResponse instance with success=false and a default error message
     */
    public static <T> ApiResponse<T> ofError() {
        return ofError("An unexpected error occurred");
    }
    
    // TODO: Add support for returning multiple error messages

    // TODO: Consider adding HTTP status code field for REST API responses
}