package com.gradle80.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination request parameters model class.
 * Used for handling pagination in API requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    /**
     * The page number (zero-based).
     */
    private int page;
    
    /**
     * Number of elements per page.
     */
    private int size;
    
    /**
     * Field name to sort by.
     */
    private String sortBy;
    
    /**
     * Direction of sorting (e.g. "ASC", "DESC").
     */
    private String sortDirection;
    
    /**
     * Creates a default pagination request instance.
     * 
     * @return A PageRequest with default values
     */
    public static PageRequest defaultInstance() {
        // Default values: first page (0), page size 10, no specific sort criteria
        return new PageRequest(0, 10, null, null);
    }
    
    /**
     * Validates if the page request has valid pagination parameters.
     * 
     * @return true if the parameters are valid, false otherwise
     */
    public boolean isValid() {
        // Page should be >= 0, size should be > 0
        return page >= 0 && size > 0;
    }
    
    /**
     * Checks if sorting parameters are specified.
     * 
     * @return true if both sortBy and sortDirection are specified, false otherwise
     */
    public boolean hasSorting() {
        return sortBy != null && !sortBy.isEmpty() && 
               sortDirection != null && !sortDirection.isEmpty();
    }
    
    /**
     * Returns the offset of the first element to be returned.
     * 
     * @return The offset calculated from page and size
     */
    public int getOffset() {
        return page * size;
    }
    
    // TODO: Add support for multiple sort fields
    
    // FIXME: Consider adding validation for sort direction values (only ASC/DESC)
}