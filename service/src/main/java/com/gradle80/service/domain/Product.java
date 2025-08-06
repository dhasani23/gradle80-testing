package com.gradle80.service.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain model representing a product in the system.
 * This class acts as an intermediary between the data layer's ProductEntity and 
 * the API layer's ProductDto.
 */
public class Product {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Default constructor
     */
    public Product() {
        // Default constructor
    }
    
    /**
     * Constructs a new Product with the specified properties.
     *
     * @param id          the product identifier
     * @param name        the product name
     * @param description the product description
     * @param price       the product price
     * @param category    the product category
     * @param available   the availability status
     * @param createdAt   the creation timestamp
     * @param updatedAt   the last update timestamp
     */
    public Product(Long id, String name, String description, BigDecimal price, 
                   String category, boolean available, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * @return the product identifier
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the product identifier to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return the availability status
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    /**
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if the product is on sale (has a defined price)
     * 
     * @return true if the product has a valid price, false otherwise
     */
    public boolean isOnSale() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Sets the product as unavailable
     */
    public void markUnavailable() {
        this.available = false;
    }
    
    /**
     * Sets the product as available
     */
    public void markAvailable() {
        this.available = true;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", available=" + available +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    // TODO: Add equals and hashCode methods
    
    /**
     * Builder pattern implementation for Product
     */
    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private boolean available = true;
        private Date createdAt;
        private Date updatedAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        public Builder category(String category) {
            this.category = category;
            return this;
        }
        
        public Builder available(boolean available) {
            this.available = available;
            return this;
        }
        
        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Product build() {
            return new Product(id, name, description, price, category, available, createdAt, updatedAt);
        }
    }
    
    /**
     * Create a new builder instance
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}