package com.gradle80.api.service;

import com.gradle80.api.dto.UserDto;
import com.gradle80.api.request.UserRequest;
import com.gradle80.api.response.UserResponse;

/**
 * User management service interface.
 * This interface defines the contract for user-related operations in the application.
 * 
 * @since 1.0
 */
public interface UserService {
    
    /**
     * Retrieves a user by their unique identifier.
     * 
     * @param userId the unique identifier of the user to retrieve
     * @return a {@link UserResponse} containing the user information if found, or error details if not found
     * @throws IllegalArgumentException if userId is null
     */
    UserResponse getUserById(Long userId);
    
    /**
     * Creates a new user in the system.
     * 
     * @param request the {@link UserRequest} containing the information for the user to create
     * @return a {@link UserResponse} containing the created user information, or error details if creation failed
     * @throws IllegalArgumentException if request is null or invalid
     * @throws RuntimeException if a user with the same username or email already exists
     */
    UserResponse createUser(UserRequest request);
    
    /**
     * Updates an existing user's information.
     * 
     * @param userId the unique identifier of the user to update
     * @param request the {@link UserRequest} containing the updated information
     * @return a {@link UserResponse} containing the updated user information, or error details if update failed
     * @throws IllegalArgumentException if userId is null or request is invalid
     * @throws RuntimeException if the user does not exist or if requested username/email conflicts with another user
     */
    UserResponse updateUser(Long userId, UserRequest request);
    
    /**
     * Deletes a user from the system.
     * Note: Depending on the implementation, this might perform a soft delete rather than permanent removal.
     * 
     * @param userId the unique identifier of the user to delete
     * @return a {@link UserResponse} indicating whether the operation was successful
     * @throws IllegalArgumentException if userId is null
     * @throws RuntimeException if the user does not exist or cannot be deleted
     */
    UserResponse deleteUser(Long userId);
    
    // TODO: Add methods for filtering and pagination of users
    
    // TODO: Add method for user activation/deactivation
    
    // FIXME: Consider adding a method for bulk operations
}