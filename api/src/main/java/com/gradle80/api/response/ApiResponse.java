package com.gradle80.api.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard API response class used across the application to provide a consistent
 * response format for all API endpoints.
 * 
 * The class supports success and error responses with customizable status codes,
 * messages, and data payloads.
 */
public class ApiResponse<T> {
    
    private final boolean success;
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final T data;
    private final Map<String, Object> metadata;
    
    /**
     * Private constructor with all fields.
     * Use the static factory methods to create instances.
     */
    private ApiResponse(boolean success, int status, String message, T data, Map<String, Object> metadata) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    /**
     * Creates a success response.
     * 
     * @param message Success message
     * @param data Data to be included in the response
     * @param <T> Type of the data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, null);
    }
    
    /**
     * Creates a success response with status code.
     * 
     * @param status HTTP status code
     * @param message Success message
     * @param data Data to be included in the response
     * @param <T> Type of the data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, null);
    }
    
    /**
     * Creates a success response with metadata.
     * 
     * @param message Success message
     * @param data Data to be included in the response
     * @param metadata Additional metadata as key-value pairs
     * @param <T> Type of the data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data, Map<String, Object> metadata) {
        return new ApiResponse<>(true, 200, message, data, metadata);
    }
    
    /**
     * Creates an error response.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @param <T> Type of the data (typically Void for error responses)
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, status, message, null, null);
    }
    
    /**
     * Creates an error response with data.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @param data Data to be included in the response
     * @param <T> Type of the data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(false, status, message, data, null);
    }
    
    /**
     * Creates an error response with metadata.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @param metadata Additional metadata as key-value pairs
     * @param <T> Type of the data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> error(int status, String message, Map<String, Object> metadata) {
        return new ApiResponse<>(false, status, message, null, metadata);
    }
    
    // Getters

    /**
     * @return Whether the response is successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return The HTTP status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return The response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return The timestamp when the response was created
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return The data payload
     */
    public T getData() {
        return data;
    }

    /**
     * @return The metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    /**
     * Adds metadata to the response.
     * 
     * @param key The metadata key
     * @param value The metadata value
     * @return This response instance for method chaining
     */
    public ApiResponse<T> addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                ", metadata=" + metadata +
                '}';
    }
}