package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product creation/update request.
 * This class represents a request to create or update a product in the system.
 * It extends the ApiRequest base class to inherit common request attributes.
 */
public class ProductRequest extends ApiRequest {
    
    /**
     * Product name
     */
    private String name;
    
    /**
     * Product description
     */
    private String description;
    
    /**
     * Product price
     */
    private BigDecimal price;
    
    /**
     * Product category
     */
    private String category;
    
    /**
     * Default constructor
     */
    public ProductRequest() {
        super();
    }
    
    /**
     * Constructor with all fields
     * 
     * @param name product name
     * @param description product description
     * @param price product price
     * @param category product category
     */
    public ProductRequest(String name, String description, BigDecimal price, String category) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
    /**
     * Gets the product name
     * 
     * @return the product name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the product name
     * 
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the product description
     * 
     * @return the product description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the product description
     * 
     * @param description the product description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the product price
     * 
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * Sets the product price
     * 
     * @param price the product price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    /**
     * Gets the product category
     * 
     * @return the product category
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Sets the product category
     * 
     * @param category the product category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * Validates the product request
     * 
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First validate the base class requirements
        if (!super.validate()) {
            return false;
        }
        
        // Validate product fields
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Description can be optional but shouldn't be just whitespace
        if (description != null && description.trim().isEmpty()) {
            return false;
        }
        
        // Price must be non-null and greater than zero
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Category is required
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        
        // TODO: Add more specific validation rules for product categories
        // FIXME: Consider implementing category validation against a predefined set of categories
        
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductRequest that = (ProductRequest) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(price, that.price) &&
               Objects.equals(category, that.category);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, price, category);
    }
    
    @Override
    public String toString() {
        return "ProductRequest{" +
               "requestId='" + getRequestId() + '\'' +
               ", timestamp=" + getTimestamp() +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", category='" + category + '\'' +
               '}';
    }
}