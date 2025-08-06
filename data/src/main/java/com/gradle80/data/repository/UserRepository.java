package com.gradle80.data.repository;

import com.gradle80.data.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for user operations.
 * Provides methods for searching and filtering users in the database.
 * Extends the CustomJpaRepository to inherit common repository functionality.
 */
@Repository
public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {

    /**
     * Find a user by their unique username.
     * 
     * @param username the username to search for (case-sensitive)
     * @return the found UserEntity or null if not found
     */
    UserEntity findByUsername(String username);
    
    /**
     * Find a user by their unique email address.
     * 
     * @param email the email address to search for (case-sensitive)
     * @return the found UserEntity or null if not found
     */
    UserEntity findByEmail(String email);
    
    /**
     * Find all users with the specified active status.
     * 
     * @param active true for active users, false for inactive users
     * @return a list of UserEntity objects matching the active status
     */
    List<UserEntity> findAllByActive(boolean active);
    
    // TODO: Add method to find users by role once role management is implemented
    
    // TODO: Add method for case-insensitive username search
    
    /**
     * Count users with the specified active status.
     * 
     * @param active true for active users, false for inactive users
     * @return the count of users matching the active status
     */
    long countByActive(boolean active);
}