package com.gradle80.api.response;

import com.gradle80.api.dto.ProductDto;
import com.gradle80.api.model.ApiResponse;

/**
 * Product operation response.
 * This class extends ApiResponse to provide product-specific response data.
 * It contains product information returned from product-related operations.
 * 
 * @since 1.0
 */
public class ProductResponse extends ApiResponse {
    
    /**
     * The product data associated with this response.
     */
    private ProductDto product;
    
    /**
     * Default constructor.
     * Initializes an empty response with default values.
     */
    public ProductResponse() {
        super();
    }
    
    /**
     * Constructs a new product response with success status and message.
     * 
     * @param success whether the operation was successful
     * @param message response message
     */
    public ProductResponse(boolean success, String message) {
        super(success, message);
    }
    
    /**
     * Constructs a new product response with success status, message, and product data.
     * 
     * @param success whether the operation was successful
     * @param message response message
     * @param product the product data
     */
    public ProductResponse(boolean success, String message, ProductDto product) {
        super(success, message);
        this.product = product;
    }
    
    /**
     * Get product data
     * 
     * @return the product data
     */
    public ProductDto getProduct() {
        return product;
    }
    
    /**
     * Set product data
     * 
     * @param product the product data
     */
    public void setProduct(ProductDto product) {
        this.product = product;
    }
    
    /**
     * Factory method to create a successful product response.
     * 
     * @param message the success message
     * @param product the product data
     * @return a new ProductResponse instance with success=true
     */
    public static ProductResponse success(String message, ProductDto product) {
        return new ProductResponse(true, message, product);
    }
    
    /**
     * Factory method to create an error product response.
     * 
     * @param message the error message
     * @return a new ProductResponse instance with success=false and no product data
     */
    public static ProductResponse error(String message) {
        return new ProductResponse(false, message);
    }
    
    @Override
    public String toString() {
        return "ProductResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", product=" + product +
                '}';
    }
    
    // TODO: Implement equals() and hashCode() methods for object comparison
    
    /**
     * Checks if this response contains product data.
     * 
     * @return true if the response contains product data, false otherwise
     */
    public boolean hasProduct() {
        return product != null;
    }
    
    /**
     * Creates a copy of this response with updated product data.
     * 
     * @param updatedProduct the updated product data
     * @return a new ProductResponse with the same status and message but updated product data
     */
    public ProductResponse withProduct(ProductDto updatedProduct) {
        return new ProductResponse(isSuccess(), getMessage(), updatedProduct);
    }
    
    // FIXME: Consider adding validation to ensure product data is consistent with response status
}