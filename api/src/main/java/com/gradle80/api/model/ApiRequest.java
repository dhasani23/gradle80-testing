package com.gradle80.api.model;

import java.util.UUID;
import java.util.Objects;

/**
 * Base class for all API request objects.
 * This class provides common properties and validation methods that
 * should be used by all API requests in the system.
 */
public abstract class ApiRequest {
    
    /**
     * Unique identifier for the request
     */
    private String requestId;
    
    /**
     * Timestamp when the request was created (epoch milliseconds)
     */
    private Long timestamp;
    
    /**
     * Default constructor that initializes the request with
     * a random UUID and current timestamp
     */
    public ApiRequest() {
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructor with specified request ID and timestamp
     * 
     * @param requestId the unique identifier for this request
     * @param timestamp the timestamp for when this request was created
     */
    public ApiRequest(String requestId, Long timestamp) {
        this.requestId = requestId;
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the request ID
     * 
     * @return the request's unique identifier
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Sets the request ID
     * 
     * @param requestId the unique identifier to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * Gets the request timestamp
     * 
     * @return the timestamp when the request was created
     */
    public Long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the request timestamp
     * 
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Validates the request. This method should be overridden
     * by subclasses to provide specific validation logic.
     * 
     * @return true if the request is valid, false otherwise
     */
    public boolean validate() {
        // Base validation checks that required fields are present
        return requestId != null && !requestId.isEmpty() && 
               timestamp != null && timestamp > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiRequest that = (ApiRequest) o;
        return Objects.equals(requestId, that.requestId) &&
               Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(requestId, timestamp);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}