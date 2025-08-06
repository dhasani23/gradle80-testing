package com.gradle80.service.event;

/**
 * Event representing user deletion.
 * This event is triggered when a user account is deleted from the system.
 * It contains minimal information needed for downstream processors to handle
 * user deletion operations such as cleanup of related data.
 */
public class UserDeletedEvent {
    
    private Long userId;
    private Long timestamp;
    
    /**
     * Default constructor for serialization frameworks.
     */
    public UserDeletedEvent() {
        // Required by serialization frameworks
    }
    
    /**
     * Creates a new user deleted event with the specified user ID.
     * The timestamp will be set to the current system time.
     *
     * @param userId the identifier of the deleted user
     */
    public UserDeletedEvent(Long userId) {
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Creates a new user deleted event with the specified user ID and timestamp.
     *
     * @param userId the identifier of the deleted user
     * @param timestamp the time when the user was deleted
     */
    public UserDeletedEvent(Long userId, Long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }
    
    /**
     * Returns the identifier of the deleted user.
     *
     * @return the user identifier
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Sets the identifier of the deleted user.
     *
     * @param userId the user identifier
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Returns the timestamp when the user was deleted.
     *
     * @return the deletion timestamp in milliseconds since epoch
     */
    public Long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the timestamp when the user was deleted.
     *
     * @param timestamp the deletion timestamp in milliseconds since epoch
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "UserDeletedEvent{" +
                "userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
    
    // TODO: Consider adding equals and hashCode methods if needed for testing or comparisons
    
    /**
     * Creates a new builder for constructing UserDeletedEvent instances.
     *
     * @return a new event builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for creating UserDeletedEvent instances.
     */
    public static class Builder {
        private Long userId;
        private Long timestamp;
        
        /**
         * Sets the user identifier.
         *
         * @param userId the user identifier
         * @return this builder for method chaining
         */
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        /**
         * Sets the deletion timestamp.
         * If not specified, current time will be used.
         *
         * @param timestamp the deletion timestamp
         * @return this builder for method chaining
         */
        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Builds a new UserDeletedEvent with the configured properties.
         *
         * @return the constructed UserDeletedEvent
         */
        public UserDeletedEvent build() {
            UserDeletedEvent event = new UserDeletedEvent();
            event.userId = this.userId;
            event.timestamp = this.timestamp != null ? this.timestamp : System.currentTimeMillis();
            return event;
        }
    }
}