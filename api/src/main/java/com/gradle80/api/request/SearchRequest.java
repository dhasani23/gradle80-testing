package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;
import java.util.List;
import java.util.Objects;

/**
 * Search request model used for performing searches across the system.
 * This class extends ApiRequest to inherit common request properties and behavior.
 */
public class SearchRequest extends ApiRequest {
    
    /**
     * The search query string
     */
    private String query;
    
    /**
     * List of categories to filter the search results
     */
    private List<String> categories;
    
    /**
     * Page number for pagination (0-indexed)
     */
    private Integer page;
    
    /**
     * Size of each page for pagination
     */
    private Integer size;
    
    /**
     * Default constructor
     */
    public SearchRequest() {
        super();
    }
    
    /**
     * Constructor with all fields
     * 
     * @param query the search query string
     * @param categories list of categories to filter results
     * @param page page number (0-indexed)
     * @param size page size
     */
    public SearchRequest(String query, List<String> categories, Integer page, Integer size) {
        super();
        this.query = query;
        this.categories = categories;
        this.page = page;
        this.size = size;
    }
    
    /**
     * Gets the search query
     * 
     * @return the search query string
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Sets the search query
     * 
     * @param query the search query string to set
     */
    public void setQuery(String query) {
        this.query = query;
    }
    
    /**
     * Gets the categories filter
     * 
     * @return list of categories to filter by
     */
    public List<String> getCategories() {
        return categories;
    }
    
    /**
     * Sets the categories filter
     * 
     * @param categories list of categories to filter by
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    /**
     * Gets the page number
     * 
     * @return the page number for pagination
     */
    public Integer getPage() {
        return page;
    }
    
    /**
     * Sets the page number
     * 
     * @param page the page number for pagination
     */
    public void setPage(Integer page) {
        this.page = page;
    }
    
    /**
     * Gets the page size
     * 
     * @return the size of each page
     */
    public Integer getSize() {
        return size;
    }
    
    /**
     * Sets the page size
     * 
     * @param size the size of each page
     */
    public void setSize(Integer size) {
        this.size = size;
    }
    
    /**
     * Validates the search request
     * Ensures that query is not null or empty and that pagination parameters are valid
     * 
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First validate the base class requirements
        if (!super.validate()) {
            return false;
        }
        
        // Query is required
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        
        // Page number should be non-negative
        if (page != null && page < 0) {
            return false;
        }
        
        // Page size should be positive
        if (size != null && size <= 0) {
            return false;
        }
        
        // FIXME: Implement more advanced validation of query syntax
        
        // TODO: Add validation for category names against a predefined list
        
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchRequest that = (SearchRequest) o;
        return Objects.equals(query, that.query) &&
               Objects.equals(categories, that.categories) &&
               Objects.equals(page, that.page) &&
               Objects.equals(size, that.size);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), query, categories, page, size);
    }
    
    @Override
    public String toString() {
        return "SearchRequest{" +
                "query='" + query + '\'' +
                ", categories=" + categories +
                ", page=" + page +
                ", size=" + size +
                "} " + super.toString();
    }
}