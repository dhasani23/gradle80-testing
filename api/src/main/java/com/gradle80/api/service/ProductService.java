package com.gradle80.api.service;

import com.gradle80.api.request.ProductRequest;
import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.ProductResponse;
import com.gradle80.api.response.SearchResponse;

/**
 * Product management service interface.
 * Defines the contract for product-related operations in the system.
 * 
 * This interface provides methods for creating, retrieving, updating, and deleting products,
 * as well as searching for products based on various criteria.
 * 
 * @since 1.0
 */
public interface ProductService {
    
    /**
     * Retrieves a product by its unique identifier.
     * 
     * @param productId the unique identifier of the product
     * @return a ProductResponse containing the product information if found,
     *         or an error response if not found
     */
    ProductResponse getProductById(Long productId);
    
    /**
     * Creates a new product based on the provided request.
     * 
     * @param request the ProductRequest containing the product details
     * @return a ProductResponse containing the created product information
     *         or an error response if creation failed
     */
    ProductResponse createProduct(ProductRequest request);
    
    /**
     * Updates an existing product with the provided information.
     * 
     * @param productId the unique identifier of the product to update
     * @param request the ProductRequest containing the updated product details
     * @return a ProductResponse containing the updated product information
     *         or an error response if the update failed
     */
    ProductResponse updateProduct(Long productId, ProductRequest request);
    
    /**
     * Deletes a product by its unique identifier.
     * 
     * @param productId the unique identifier of the product to delete
     * @return a ProductResponse indicating success or failure of the deletion
     */
    ProductResponse deleteProduct(Long productId);
    
    /**
     * Searches for products based on criteria specified in the request.
     * The search may include text-based queries, category filters, and pagination.
     * 
     * @param request the SearchRequest containing search criteria and pagination info
     * @return a SearchResponse containing the search results and pagination metadata
     */
    SearchResponse searchProducts(SearchRequest request);
    
    // TODO: Add batch operations for product management
    
    // TODO: Consider adding methods for product inventory management
    
    // FIXME: Product image handling should be addressed in future versions
}