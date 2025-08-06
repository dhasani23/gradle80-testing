package com.gradle80.api.service;

import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.SearchResponse;

import java.util.List;

/**
 * Search service interface.
 * 
 * This interface defines the contract for search operations across the system.
 * Implementations should handle the processing of search queries and return
 * appropriate search results according to various criteria.
 * 
 * @since 1.0
 */
public interface SearchService {
    
    /**
     * Performs a search operation based on the provided search criteria.
     * 
     * This method processes search requests containing query text, optional filters, 
     * and pagination parameters to find and return relevant results.
     * 
     * @param request the search request containing query parameters and filters
     * @return a SearchResponse containing the search results and metadata
     * @throws IllegalArgumentException if the request is invalid
     */
    SearchResponse search(SearchRequest request);
    
    /**
     * Lists all available search categories supported by the system.
     * 
     * The returned categories can be used as filters in search requests
     * to narrow down search results to specific domains or types.
     * 
     * @return a list of category names available for filtering search results
     */
    List<String> getCategories();
    
    // TODO: Add methods for advanced search features like faceted search
    
    // TODO: Consider adding search analytics or trending queries methods
    
    // FIXME: Current search implementation may have performance issues with large datasets
}