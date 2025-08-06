package com.gradle80.api.response;

import com.gradle80.api.dto.UserDto;
import com.gradle80.api.model.ApiResponse;

/**
 * User operation response.
 * This class extends ApiResponse to provide user-specific response data.
 * It contains user information returned from user-related operations.
 * 
 * @since 1.0
 */
public class UserResponse extends ApiResponse {
    
    /**
     * The user data associated with this response.
     */
    private UserDto user;
    
    /**
     * Default constructor.
     * Initializes an empty response with default values.
     */
    public UserResponse() {
        super();
    }
    
    /**
     * Constructs a new user response with success status and message.
     * 
     * @param success whether the operation was successful
     * @param message response message
     */
    public UserResponse(boolean success, String message) {
        super(success, message);
    }
    
    /**
     * Constructs a new user response with success status, message, and user data.
     * 
     * @param success whether the operation was successful
     * @param message response message
     * @param user the user data
     */
    public UserResponse(boolean success, String message, UserDto user) {
        super(success, message);
        this.user = user;
    }
    
    /**
     * Get user data
     * 
     * @return the user data
     */
    public UserDto getUser() {
        return user;
    }
    
    /**
     * Set user data
     * 
     * @param user the user data
     */
    public void setUser(UserDto user) {
        this.user = user;
    }
    
    /**
     * Factory method to create a successful user response.
     * 
     * @param message the success message
     * @param user the user data
     * @return a new UserResponse instance with success=true
     */
    public static UserResponse success(String message, UserDto user) {
        return new UserResponse(true, message, user);
    }
    
    /**
     * Factory method to create an error user response.
     * 
     * @param message the error message
     * @return a new UserResponse instance with success=false and no user data
     */
    public static UserResponse error(String message) {
        return new UserResponse(false, message);
    }
    
    @Override
    public String toString() {
        return "UserResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", user=" + user +
                '}';
    }
    
    // TODO: Implement equals() and hashCode() methods
    
    /**
     * Checks if this response contains user data.
     * 
     * @return true if the response contains user data, false otherwise
     */
    public boolean hasUser() {
        return user != null;
    }
    
    /**
     * Creates a copy of this response with updated user data.
     * 
     * @param updatedUser the updated user data
     * @return a new UserResponse with the same status and message but updated user data
     */
    public UserResponse withUser(UserDto updatedUser) {
        return new UserResponse(isSuccess(), getMessage(), updatedUser);
    }
}