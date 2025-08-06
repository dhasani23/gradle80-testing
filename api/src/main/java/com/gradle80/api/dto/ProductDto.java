package com.gradle80.api.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data transfer object for product information.
 * This class represents product data that is transferred between different layers
 * of the application and in API responses.
 */
public class ProductDto {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;
    
    /**
     * Default constructor
     */
    public ProductDto() {
    }
    
    /**
     * Parameterized constructor with essential fields
     * 
     * @param id Product identifier
     * @param name Product name
     * @param price Product price
     */
    public ProductDto(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = true;
    }
    
    /**
     * Fully parameterized constructor
     * 
     * @param id Product identifier
     * @param name Product name
     * @param description Product description
     * @param price Product price
     * @param category Product category
     * @param available Availability status
     */
    public ProductDto(Long id, String name, String description, 
                      BigDecimal price, String category, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }
    
    /**
     * Get the product identifier
     * 
     * @return the product id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Set the product identifier
     * 
     * @param id the product id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Get the product name
     * 
     * @return the product name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the product name
     * 
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the product description
     * 
     * @return the product description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the product description
     * 
     * @param description the product description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get the product price
     * 
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * Set the product price
     * 
     * @param price the product price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    /**
     * Get the product category
     * 
     * @return the product category
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Set the product category
     * 
     * @param category the product category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * Check if the product is available
     * 
     * @return the availability status
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Set the product availability status
     * 
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category);
    }

    @Override
    public String toString() {
        return "ProductDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + (description != null ? description.substring(0, Math.min(description.length(), 20)) + "..." : null) + '\'' +
               ", price=" + price +
               ", category='" + category + '\'' +
               ", available=" + available +
               '}';
    }
}