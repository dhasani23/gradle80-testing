package com.gradle80.api.model;

/**
 * Base class for all API response objects.
 * This class provides common fields and behaviors that all API responses should have.
 */
public class ApiResponse {
    
    /**
     * Indicates whether the API request was successful.
     */
    private boolean success;
    
    /**
     * Contains a descriptive message about the response.
     * In case of errors, this typically contains error details.
     */
    private String message;
    
    /**
     * The timestamp when the response was generated (in milliseconds since epoch).
     */
    private Long timestamp;
    
    /**
     * Default constructor.
     * Initializes a response with default values and current timestamp.
     */
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructs a new API response with the given parameters.
     *
     * @param success   whether the operation was successful
     * @param message   response message
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructs a new API response with the given parameters.
     *
     * @param success   whether the operation was successful
     * @param message   response message
     * @param timestamp response timestamp
     */
    public ApiResponse(boolean success, String message, Long timestamp) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    /**
     * Factory method to create a successful response.
     *
     * @param message the success message
     * @return a new ApiResponse instance with success=true
     */
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }
    
    /**
     * Factory method to create an error response.
     *
     * @param message the error message
     * @return a new ApiResponse instance with success=false
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }
    
    /**
     * @return whether the operation was successful
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * @param success whether the operation was successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * @return the response message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param message the response message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return the timestamp when the response was generated
     */
    public Long getTimestamp() {
        return timestamp;
    }
    
    /**
     * @param timestamp the timestamp when the response was generated
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    // TODO: Consider implementing equals() and hashCode() methods if needed for comparison
    
    /**
     * Updates the timestamp to the current time.
     * Useful when reusing response objects.
     */
    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }
}