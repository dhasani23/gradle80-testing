package com.gradle80.web.controller;

import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.SearchResponse;
import com.gradle80.api.service.SearchService;
import com.gradle80.web.filter.ResponseEnhancer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for search operations.
 * 
 * This controller provides endpoints for searching products and retrieving
 * available product categories.
 */
@RestController
@RequestMapping("/api/search")
@Api(tags = "Search", description = "Search operations API")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SearchController {

    /**
     * Search service for performing search operations
     */
    private final SearchService searchService;
    
    /**
     * Response enhancer for standardizing API responses
     */
    private final ResponseEnhancer responseEnhancer;

    /**
     * Performs a search operation based on the provided search criteria.
     *
     * @param request the search request containing search parameters
     * @return a ResponseEntity containing search results
     */
    @PostMapping
    @ApiOperation(value = "Search products", notes = "Search products based on query and filters")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Search completed successfully"),
        @ApiResponse(code = 400, message = "Invalid search parameters"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<SearchResponse> search(
            @ApiParam(value = "Search request parameters", required = true)
            @Valid @RequestBody SearchRequest request) {
        
        log.debug("REST request to search with criteria: {}", request);
        
        try {
            // Validate query parameter
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                log.warn("Search request received with empty query");
                return ResponseEntity.badRequest().build();
            }
            
            // Set default pagination if not provided
            if (request.getPage() == null) {
                request.setPage(0);
            }
            if (request.getSize() == null) {
                request.setSize(20);
            }
            
            // Perform search operation
            SearchResponse response = searchService.search(request);
            
            // FIXME: Handle case when search service returns null
            if (response == null) {
                log.error("Search service returned null response for query: {}", request.getQuery());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            // Enhance and return response
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error performing search operation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves all available product categories.
     *
     * @return a ResponseEntity containing list of category names
     */
    @GetMapping("/categories")
    @ApiOperation(value = "Get all categories", notes = "Retrieve all available product categories")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Categories retrieved successfully"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<String>> getCategories() {
        
        log.debug("REST request to get all product categories");
        
        try {
            // Retrieve categories from service
            List<String> categories = searchService.getCategories();
            
            // TODO: Add caching for categories to improve performance
            
            // Enhance and return response
            return responseEnhancer.enhance(categories, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving product categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}