package com.gradle80.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Custom repository base interface that extends JpaRepository to provide
 * common functionality across all repositories, particularly for
 * handling active/inactive entities and soft deletion.
 *
 * @param <T> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 */
@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Finds all active entities.
     * This method assumes entities have an 'active' flag or similar
     * mechanism to determine if they are active.
     * 
     * @return a list of all active entities
     */
    List<T> findAllActive();
    
    /**
     * Performs a soft delete on the entity with the given ID.
     * Instead of removing the record from the database, this method
     * should update the entity to mark it as inactive or deleted.
     * 
     * @param id the ID of the entity to soft delete
     * @throws IllegalArgumentException if the ID is null
     * @throws javax.persistence.EntityNotFoundException if no entity with the given ID exists
     */
    void softDelete(Long id);
    
    // TODO: Consider adding methods for batch soft delete operations
    
    // FIXME: Method should handle entities without 'active' property appropriately
}