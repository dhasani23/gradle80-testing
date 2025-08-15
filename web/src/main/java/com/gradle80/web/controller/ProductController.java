package com.gradle80.web.controller;

import com.gradle80.api.request.ProductRequest;
import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.ProductResponse;
import com.gradle80.api.response.SearchResponse;
import com.gradle80.api.service.ProductService;
import com.gradle80.web.filter.ResponseEnhancer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * REST controller for product operations.
 * This controller handles CRUD operations for products and product search functionality.
 * 
 * @author gradle80-generator
 * @version 1.0
 */
@RestController
@RequestMapping("/api/products")
@Api(value = "Product Management", description = "Operations for managing products")
@Validated
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    private final ResponseEnhancer responseEnhancer;
    
    /**
     * Constructor for ProductController.
     *
     * @param productService service for product operations
     * @param responseEnhancer enhancer for API responses
     */
    @Autowired
    public ProductController(ProductService productService, ResponseEnhancer responseEnhancer) {
        this.productService = productService;
        this.responseEnhancer = responseEnhancer;
    }
    
    /**
     * Retrieves product by ID.
     *
     * @param productId ID of the product to retrieve
     * @return ResponseEntity containing the product data
     */
    @GetMapping("/{productId}")
    @ApiOperation(value = "Get product by ID", response = ProductResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved product"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @ApiParam(value = "Product ID", required = true) 
            @PathVariable @NotNull Long productId) {
        
        logger.debug("REST request to get Product with ID: {}", productId);
        
        ProductResponse response = productService.getProductById(productId);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }
    
    /**
     * Creates a new product.
     *
     * @param request product creation request
     * @return ResponseEntity containing the created product data
     */
    @PostMapping
    @ApiOperation(value = "Create a new product", response = ProductResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Product successfully created"),
        @ApiResponse(code = 400, message = "Invalid request data"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @ApiParam(value = "Product creation request", required = true) 
            @Valid @RequestBody ProductRequest request) {
        
        logger.debug("REST request to create Product: {}", request);
        
        ProductResponse response = productService.createProduct(request);
        return responseEnhancer.enhance(response, HttpStatus.CREATED);
    }
    
    /**
     * Updates an existing product.
     *
     * @param productId ID of the product to update
     * @param request product update request
     * @return ResponseEntity containing the updated product data
     */
    @PutMapping("/{productId}")
    @ApiOperation(value = "Update an existing product", response = ProductResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product successfully updated"),
        @ApiResponse(code = 400, message = "Invalid request data"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @ApiParam(value = "Product ID", required = true) 
            @PathVariable @NotNull Long productId,
            @ApiParam(value = "Product update request", required = true) 
            @Valid @RequestBody ProductRequest request) {
        
        logger.debug("REST request to update Product with ID: {} and data: {}", productId, request);
        
        // TODO: Add validation to ensure the product exists before attempting update
        ProductResponse response = productService.updateProduct(productId, request);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }
    
    /**
     * Deletes an existing product.
     *
     * @param productId ID of the product to delete
     * @return ResponseEntity containing the deletion result
     */
    @DeleteMapping("/{productId}")
    @ApiOperation(value = "Delete a product", response = ProductResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product successfully deleted"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ProductResponse> deleteProduct(
            @ApiParam(value = "Product ID", required = true) 
            @PathVariable @NotNull Long productId) {
        
        logger.debug("REST request to delete Product with ID: {}", productId);
        
        ProductResponse response = productService.deleteProduct(productId);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }
    
    /**
     * Searches for products based on criteria.
     *
     * @param request search criteria
     * @return ResponseEntity containing the search results
     */
    @PostMapping("/search")
    @ApiOperation(value = "Search for products", response = SearchResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Search completed successfully"),
        @ApiResponse(code = 400, message = "Invalid search criteria"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<SearchResponse> searchProducts(
            @ApiParam(value = "Search criteria", required = true) 
            @Valid @RequestBody SearchRequest request) {
        
        logger.debug("REST request to search Products with criteria: {}", request);
        
        // FIXME: Consider implementing caching for frequent search queries
        SearchResponse response = productService.searchProducts(request);
        return responseEnhancer.enhance(response, HttpStatus.OK);
    }
}