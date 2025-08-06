package com.gradle80.data.repository.custom;

import com.gradle80.data.entity.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * Custom repository interface for User operations.
 * This interface defines additional methods beyond the standard CRUD operations
 * provided by Spring Data JPA for UserEntity.
 * 
 * Implementation classes should provide concrete implementations for these methods
 * using either JPA Criteria API, JPQL, native SQL queries, or other appropriate
 * data access mechanisms.
 */
public interface UserRepositoryCustom {

    /**
     * Finds active users with a specific role.
     * This method allows filtering users by their role while ensuring only active accounts
     * are included in the results.
     *
     * @param role The user role to filter by
     * @return A list of active UserEntity objects with the specified role
     */
    List<UserEntity> findActiveUsersByRole(String role);
    
    /**
     * Updates the last login date for a specific user.
     * This method is used for tracking user activity and login sessions.
     *
     * @param userId The ID of the user to update
     * @param loginDate The date and time of the login event
     * @throws javax.persistence.EntityNotFoundException if the user cannot be found
     * @throws javax.persistence.PersistenceException if there's a database error
     */
    void updateLastLoginDate(Long userId, Date loginDate);
}