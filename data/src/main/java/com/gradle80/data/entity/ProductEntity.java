package com.gradle80.data.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Product database entity.
 * 
 * This entity represents a product in the system with properties like name,
 * description, price, category and availability status. It extends BaseEntity
 * to inherit common fields (id, createdAt, updatedAt) and behaviors.
 */
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "available", nullable = false)
    private boolean available;

    /**
     * Default constructor
     */
    public ProductEntity() {
        // Required by JPA
    }
    
    /**
     * Constructor with required fields
     * 
     * @param name product name
     * @param price product price
     * @param available availability status
     */
    public ProductEntity(String name, BigDecimal price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }
    
    /**
     * Full constructor
     * 
     * @param name product name
     * @param description product description
     * @param price product price
     * @param category product category
     * @param available availability status
     */
    public ProductEntity(String name, String description, BigDecimal price, 
                        String category, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    /**
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the product description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the product price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the product category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return true if the product is available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * @return true if the product is available, false otherwise
     */
    public boolean getAvailable() {
        return available;
    }

    /**
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // NOTE: createdAt and updatedAt are inherited from BaseEntity

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(description.length(), 20)) + "..." : null) + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", available=" + available +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
    
    /**
     * Updates the price of the product and validates that the new price is not negative.
     * 
     * @param newPrice the new price to set
     * @throws IllegalArgumentException if the price is negative
     */
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        this.price = newPrice;
    }
    
    /**
     * Checks if this product belongs to the specified category.
     * 
     * @param categoryName the category to check
     * @return true if the product belongs to the category, false otherwise
     */
    public boolean isInCategory(String categoryName) {
        return categoryName != null && categoryName.equalsIgnoreCase(this.category);
    }
    
    /**
     * Factory method to create a copy of this product with a different price.
     * Useful for creating variants of the same product.
     * 
     * @param newPrice the price for the new product variant
     * @return a new product instance with the same properties but different price
     */
    public ProductEntity createVariant(BigDecimal newPrice) {
        // TODO: Implement proper deep copying logic if needed
        return new ProductEntity(this.name, this.description, newPrice, this.category, this.available);
    }
}