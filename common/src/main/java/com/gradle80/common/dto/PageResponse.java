package com.gradle80.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.List;

/**
 * Pagination response wrapper that contains page content and pagination metadata.
 * This class is used to wrap API responses that involve pagination.
 * 
 * @param <T> The type of elements in the page content
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /**
     * The content of the current page.
     */
    private List<T> content;
    
    /**
     * The total number of elements across all pages.
     */
    private long totalElements;
    
    /**
     * The total number of pages.
     */
    private int totalPages;
    
    /**
     * The current page number (0-based).
     */
    private int currentPage;
    
    /**
     * The size of each page.
     */
    private int size;
    
    /**
     * Checks if the current page has any content.
     *
     * @return true if the page has content, false otherwise
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }
    
    /**
     * Checks if this is the first page.
     *
     * @return true if this is the first page, false otherwise
     */
    public boolean isFirstPage() {
        return currentPage == 0;
    }
    
    /**
     * Checks if this is the last page.
     *
     * @return true if this is the last page, false otherwise
     */
    public boolean isLastPage() {
        return currentPage == totalPages - 1 || totalPages == 0;
    }
    
    /**
     * Creates a new empty page response.
     *
     * @param <E> the type of elements in the page
     * @return an empty page response
     */
    public static <E> PageResponse<E> empty() {
        return new PageResponse<>(Collections.emptyList(), 0L, 0, 0, 0);
    }
    
    /**
     * Returns the number of elements in the current page.
     * 
     * @return the number of elements in the current page
     */
    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }
    
    /**
     * Checks if there is a next page available.
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }
    
    /**
     * Checks if there is a previous page available.
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
    
    // TODO: Add methods to convert from/to Spring Page object if Spring Data is used
}