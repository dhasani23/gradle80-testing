package com.gradle80.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base data transfer object with common fields that are shared across all DTOs.
 * This class serves as the foundation for data transfer objects in the application.
 * 
 * @author Gradle80 Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto {
    
    /**
     * Unique identifier for the DTO.
     * This field typically matches the ID of the corresponding entity.
     */
    private Long id;
    
    /**
     * Timestamp indicating when the underlying entity was created.
     * This field is automatically populated during entity creation.
     */
    private LocalDateTime createdDate;
    
    /**
     * Timestamp indicating when the underlying entity was last modified.
     * This field is automatically updated whenever the entity changes.
     */
    private LocalDateTime lastModifiedDate;
    
    // TODO: Consider adding version field for optimistic locking
    
    // TODO: Add utility methods for DTO to entity conversion if needed
}