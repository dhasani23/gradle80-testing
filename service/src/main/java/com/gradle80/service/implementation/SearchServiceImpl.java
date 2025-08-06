package com.gradle80.service.implementation;

import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.SearchResponse;
import com.gradle80.api.service.SearchService;
import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.repository.ProductRepository;
import com.gradle80.service.mapper.ProductMapper;
import com.gradle80.service.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of search service providing product search functionality
 * and category listing.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Constructs a new SearchServiceImpl with required dependencies.
     *
     * @param productRepository Repository for product data access
     * @param productMapper Mapper for converting between entities and DTOs
     */
    @Autowired
    public SearchServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Performs a search for products based on the provided search criteria.
     * Supports filtering by query terms and categories, with pagination.
     *
     * @param request The search request containing search parameters
     * @return SearchResponse containing the search results and metadata
     */
    @Override
    public SearchResponse search(SearchRequest request) {
        // Default page and size if not provided
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        
        // Create pageable for pagination
        Pageable pageable = PageRequest.of(page, size);
        
        List<ProductEntity> results = new ArrayList<>();
        long totalCount = 0;
        
        // Determine search strategy based on provided parameters
        if (StringUtils.hasText(request.getQuery()) && request.getCategories() != null && !request.getCategories().isEmpty()) {
            // Search by both query and categories
            // TODO: Implement optimized search with both query and category filters
            // This is a simple implementation that could be enhanced with QueryDSL or a custom repository method
            results = productRepository.findByNameContainingIgnoreCase(request.getQuery())
                    .stream()
                    .filter(product -> request.getCategories().contains(product.getCategory()))
                    .collect(Collectors.toList());
            totalCount = results.size();
            
            // Apply pagination manually (not efficient for large datasets)
            // FIXME: Replace with a proper paginated query using a custom repository method
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), results.size());
            
            if (start < end) {
                results = results.subList(start, end);
            } else {
                results = new ArrayList<>();
            }
        } else if (StringUtils.hasText(request.getQuery())) {
            // Search by query only
            results = productRepository.findByNameContainingIgnoreCase(request.getQuery());
            totalCount = results.size();
            
            // Apply pagination manually
            // FIXME: Same as above, needs optimization
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), results.size());
            
            if (start < end) {
                results = results.subList(start, end);
            } else {
                results = new ArrayList<>();
            }
        } else if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            // Search by categories only
            // For simplicity, we're just using the first category in the list
            // TODO: Enhance to properly handle multiple categories with union/intersection logic
            String category = request.getCategories().get(0);
            results = productRepository.findByCategory(category);
            totalCount = results.size();
            
            // Apply pagination manually
            // FIXME: Same as above, needs optimization
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), results.size());
            
            if (start < end) {
                results = results.subList(start, end);
            } else {
                results = new ArrayList<>();
            }
        } else {
            // No specific search criteria, return all available products
            // FIXME: This could return too many results, should be paginated at the database level
            results = productRepository.findAllByAvailable(true);
            totalCount = results.size();
            
            // Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), results.size());
            
            if (start < end) {
                results = results.subList(start, end);
            } else {
                results = new ArrayList<>();
            }
        }
        
        // Calculate total pages based on total count and page size
        int totalPages = size > 0 ? (int) Math.ceil((double) totalCount / size) : 0;
        
        // Create the response
        SearchResponse response = new SearchResponse();
        response.setSuccess(true);
        response.setMessage("Search completed successfully");
        response.setTimestamp(System.currentTimeMillis());
        response.setResults(results.stream()
                .map(productEntity -> {
                    Product product = productMapper.fromEntity(productEntity);
                    return productMapper.toDto(product);
                })
                .collect(Collectors.toList()));
        response.setTotalHits(totalCount);
        response.setTotalPages(totalPages);
        
        return response;
    }

    /**
     * Returns a list of all distinct product categories available in the system.
     *
     * @return List of unique category names
     */
    @Override
    public List<String> getCategories() {
        // Retrieve all products
        List<ProductEntity> products = productRepository.findAll();
        
        // Extract distinct categories
        return products.stream()
                .map(ProductEntity::getCategory)
                .filter(StringUtils::hasText)  // Filter out null or empty categories
                .distinct()
                .sorted()  // Sort alphabetically for better UX
                .collect(Collectors.toList());
    }
}