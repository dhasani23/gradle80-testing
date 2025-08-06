package com.gradle80.data.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Implementation of the CustomJpaRepository interface that extends SimpleJpaRepository
 * to provide common functionality across all repositories, particularly for
 * handling active/inactive entities and soft deletion.
 *
 * @param <T> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 */
public class CustomJpaRepositoryImpl<T, ID extends Serializable> 
        extends SimpleJpaRepository<T, ID> 
        implements CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> domainClass;

    /**
     * Constructor that takes JPA entity information and entity manager.
     *
     * @param entityInformation information about the entity
     * @param entityManager JPA entity manager instance
     */
    public CustomJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.domainClass = entityInformation.getJavaType();
    }

    /**
     * {@inheritDoc}
     * 
     * Finds all active entities by querying for entities where the 'active' property is true.
     * Uses JPA Criteria API for dynamic query building.
     */
    @Override
    public List<T> findAllActive() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        
        try {
            // Add condition: active = true
            query.where(builder.equal(root.get("active"), true));
            return entityManager.createQuery(query).getResultList();
        } catch (IllegalArgumentException e) {
            // FIXME: Handle case where entity doesn't have 'active' property
            // For now, return all entities if 'active' property doesn't exist
            return findAll();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Performs soft deletion by setting the 'active' property to false instead of
     * physically removing the entity from the database.
     */
    @Override
    @Transactional
    public void softDelete(Long id) {
        Assert.notNull(id, "The given id must not be null!");
        
        // Find entity by id
        T entity = findById((ID) id).orElseThrow(() -> 
            new EntityNotFoundException("Entity with id " + id + " not found"));
        
        try {
            // Use reflection to set active = false
            Field activeField = domainClass.getDeclaredField("active");
            activeField.setAccessible(true);
            activeField.set(entity, false);
            
            // Save the entity back to the database
            save(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle case where entity doesn't have 'active' field
            throw new UnsupportedOperationException(
                "Soft delete not supported for entity " + domainClass.getSimpleName() +
                ". Entity must have 'active' field.", e);
        }
    }
    
    /**
     * Helper method to check if an entity has an 'active' property.
     * 
     * @return true if the entity has an 'active' property, false otherwise
     */
    private boolean hasActiveProperty() {
        try {
            domainClass.getDeclaredField("active");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
    
    // TODO: Implement batch soft delete operations
    
    // TODO: Consider adding a method to restore soft-deleted entities
}