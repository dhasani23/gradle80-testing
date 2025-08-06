package com.gradle80.service.event;

import java.util.Objects;

/**
 * Event representing user creation.
 * 
 * This class is used to notify other components within the system
 * when a new user has been created. It contains essential information
 * about the created user and the timestamp of creation.
 */
public class UserCreatedEvent {
    
    private final Long userId;
    private final String username;
    private final Long timestamp;
    
    /**
     * Constructs a new UserCreatedEvent with all required fields.
     *
     * @param userId    The identifier of the newly created user
     * @param username  The username of the newly created user
     * @param timestamp The timestamp when the user was created
     */
    public UserCreatedEvent(Long userId, String username, Long timestamp) {
        this.userId = userId;
        this.username = username;
        this.timestamp = timestamp;
    }
    
    /**
     * Constructs a new UserCreatedEvent with user information and current timestamp.
     *
     * @param userId   The identifier of the newly created user
     * @param username The username of the newly created user
     */
    public UserCreatedEvent(Long userId, String username) {
        this(userId, username, System.currentTimeMillis());
    }
    
    /**
     * Gets the user identifier.
     *
     * @return The user identifier
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Gets the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the event timestamp.
     *
     * @return The timestamp when this event was created
     */
    public Long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserCreatedEvent that = (UserCreatedEvent) o;
        
        if (!Objects.equals(userId, that.userId)) return false;
        if (!Objects.equals(username, that.username)) return false;
        return Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "UserCreatedEvent{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}