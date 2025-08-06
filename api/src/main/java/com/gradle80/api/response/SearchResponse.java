package com.gradle80.api.response;

import com.gradle80.api.dto.ProductDto;
import com.gradle80.api.model.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Search response model.
 * This class represents the response returned by search operations,
 * containing search results and pagination information.
 */
public class SearchResponse extends ApiResponse {
    
    /**
     * List of product search results.
     */
    private List<ProductDto> results;
    
    /**
     * Total number of hits for the search query (across all pages).
     */
    private Long totalHits;
    
    /**
     * Total number of pages available for the search query.
     */
    private Integer totalPages;
    
    /**
     * Default constructor.
     * Initializes an empty search response.
     */
    public SearchResponse() {
        super();
        this.results = new ArrayList<>();
    }
    
    /**
     * Constructs a search response with basic information.
     *
     * @param success    whether the search was successful
     * @param message    response message
     */
    public SearchResponse(boolean success, String message) {
        super(success, message);
        this.results = new ArrayList<>();
    }
    
    /**
     * Constructs a search response with complete information.
     *
     * @param success    whether the search was successful
     * @param message    response message
     * @param results    list of product search results
     * @param totalHits  total hits across all pages
     * @param totalPages total pages available
     */
    public SearchResponse(boolean success, String message, List<ProductDto> results, 
                         Long totalHits, Integer totalPages) {
        super(success, message);
        this.results = results;
        this.totalHits = totalHits;
        this.totalPages = totalPages;
    }
    
    /**
     * Factory method to create a successful search response.
     *
     * @param results    search results
     * @param totalHits  total number of hits
     * @param totalPages total number of pages
     * @return a new SearchResponse instance
     */
    public static SearchResponse success(List<ProductDto> results, Long totalHits, Integer totalPages) {
        return new SearchResponse(true, "Search completed successfully", results, totalHits, totalPages);
    }
    
    /**
     * Factory method to create an empty search response.
     *
     * @return a new SearchResponse instance with empty results
     */
    public static SearchResponse emptyResults() {
        return new SearchResponse(true, "No results found", new ArrayList<>(), 0L, 0);
    }
    
    /**
     * Get the search results
     *
     * @return list of product results
     */
    public List<ProductDto> getResults() {
        return results;
    }
    
    /**
     * Set the search results
     *
     * @param results list of product results
     */
    public void setResults(List<ProductDto> results) {
        this.results = results != null ? results : new ArrayList<>();
    }
    
    /**
     * Get the total number of hits for the search query
     *
     * @return total hits
     */
    public Long getTotalHits() {
        return totalHits;
    }
    
    /**
     * Set the total number of hits for the search query
     *
     * @param totalHits total hits
     */
    public void setTotalHits(Long totalHits) {
        this.totalHits = totalHits;
    }
    
    /**
     * Get the total number of pages available for the search query
     *
     * @return total pages
     */
    public Integer getTotalPages() {
        return totalPages;
    }
    
    /**
     * Set the total number of pages available for the search query
     *
     * @param totalPages total pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    /**
     * Checks if the search returned any results
     * 
     * @return true if results are available, false otherwise
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }
    
    /**
     * Get the number of results on the current page
     * 
     * @return number of results
     */
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        
        SearchResponse that = (SearchResponse) o;
        return Objects.equals(totalHits, that.totalHits) &&
               Objects.equals(totalPages, that.totalPages);
        // Note: Not comparing results list for efficiency, but could be added if needed
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), totalHits, totalPages);
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
               "success=" + isSuccess() +
               ", message='" + getMessage() + '\'' +
               ", timestamp=" + getTimestamp() +
               ", results.size=" + getResultCount() +
               ", totalHits=" + totalHits +
               ", totalPages=" + totalPages +
               '}';
    }
    
    // TODO: Consider implementing pagination helper methods like hasNextPage() and hasPreviousPage()
    
    /**
     * FIXME: When large result sets are returned, consider implementing lazy loading or pagination
     * to improve performance and reduce memory consumption
     */
}