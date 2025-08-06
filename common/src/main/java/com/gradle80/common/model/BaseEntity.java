package com.gradle80.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Base entity class with common fields and methods for persistence entities.
 * This class serves as the foundation for all persistent entities in the application,
 * providing common fields like id, timestamps, and version for optimistic locking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    /**
     * Unique identifier for the entity
     */
    private Long id;
    
    /**
     * Timestamp when the entity was first created
     */
    private LocalDateTime createdDate;
    
    /**
     * Timestamp when the entity was last modified
     */
    private LocalDateTime lastModifiedDate;
    
    /**
     * Entity version used for optimistic locking
     */
    private Integer version;
    
    /**
     * Pre-persist callback method.
     * This method is called before the entity is persisted to the database.
     * It sets the creation timestamp and the last modified timestamp.
     */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.lastModifiedDate = now;
        
        if (this.version == null) {
            this.version = 1;
        }
        
        // TODO: Add auditing information like created by user
    }
    
    /**
     * Pre-update callback method.
     * This method is called before the entity is updated in the database.
     * It updates the last modified timestamp and increments the version.
     */
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
        
        // FIXME: Version increment should be handled by JPA/Hibernate
        // This is a temporary solution until proper ORM integration
        if (this.version != null) {
            this.version++;
        } else {
            this.version = 1;
        }
        
        // TODO: Add auditing information like last modified by user
    }
}