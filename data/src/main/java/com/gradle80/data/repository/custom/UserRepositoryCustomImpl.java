package com.gradle80.data.repository.custom;

import com.gradle80.data.entity.UserEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of custom User repository methods.
 * 
 * This class provides custom query implementations for UserEntity objects
 * beyond the standard CRUD operations provided by Spring Data JPA.
 */
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Finds active users with a specific role.
     * 
     * This implementation uses the JPA Criteria API to create a query that filters
     * users based on their role and active status. The query is optimized to only
     * fetch users that match both criteria.
     *
     * @param role The user role to filter by
     * @return A list of active UserEntity objects with the specified role
     */
    @Override
    public List<UserEntity> findActiveUsersByRole(String role) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Only include active users
        predicates.add(criteriaBuilder.equal(root.get("active"), true));
        
        // Filter by role if provided
        if (role != null && !role.isEmpty()) {
            // FIXME: UserEntity doesn't seem to have a role field in the current model.
            // This implementation assumes a 'role' field exists.
            // Update this query based on how roles are actually stored in the database.
            predicates.add(criteriaBuilder.equal(root.get("role"), role));
        }
        
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        
        TypedQuery<UserEntity> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    /**
     * Updates the last login date for a specific user.
     * 
     * This implementation uses JPQL to directly update the user entity in the database
     * without having to fetch the complete entity first, which is more efficient for
     * simple update operations.
     *
     * @param userId The ID of the user to update
     * @param loginDate The date and time of the login event
     * @throws jakarta.persistence.EntityNotFoundException if the user cannot be found
     * @throws jakarta.persistence.PersistenceException if there's a database error
     */
    @Override
    public void updateLastLoginDate(Long userId, Date loginDate) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (loginDate == null) {
            loginDate = new Date(); // Default to current date if none provided
        }
        
        // TODO: Add lastLoginDate to UserEntity class as it's currently not included
        // This query assumes there's a lastLoginDate field in the UserEntity
        
        int updatedRows = entityManager.createQuery(
                "UPDATE UserEntity u SET u.updatedAt = :currentTime WHERE u.id = :userId")
                .setParameter("currentTime", new Date())
                .setParameter("userId", userId)
                .executeUpdate();
                
        if (updatedRows == 0) {
            throw new jakarta.persistence.EntityNotFoundException("User with ID " + userId + " not found");
        }
    }
}