package com.gradle80.test.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Domain model representing an order in the test environment.
 * This class is used specifically for testing purposes and mirrors
 * the essential functionality of the real Order domain model.
 */
public class Order {
    
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Default constructor
     */
    public Order() {
        this.productIds = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    /**
     * Creates an order with essential parameters
     * 
     * @param id the order ID
     * @param userId the user ID
     * @param status the order status
     */
    public Order(Long id, Long userId, String status) {
        this();
        this.id = id;
        this.userId = userId;
        this.status = status;
    }
    
    /**
     * Full constructor with all parameters
     * 
     * @param id the order ID
     * @param userId the user ID
     * @param productIds list of product IDs in the order
     * @param totalAmount the total order amount
     * @param status the order status
     * @param shippingAddress the shipping address
     * @param createdAt when the order was created
     * @param updatedAt when the order was last updated
     */
    public Order(Long id, Long userId, List<Long> productIds, BigDecimal totalAmount, 
                String status, String shippingAddress, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productIds = productIds != null ? productIds : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt != null ? createdAt : new Date();
        this.updatedAt = updatedAt != null ? updatedAt : new Date();
    }

    /**
     * @return the order identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the order identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the user identifier
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the user identifier to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the list of product identifiers
     */
    public List<Long> getProductIds() {
        return productIds;
    }

    /**
     * @param productIds the list of product identifiers to set
     */
    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds != null ? productIds : new ArrayList<>();
    }

    /**
     * Add a product to the order
     * 
     * @param productId the product identifier to add
     * @return true if the product was added
     */
    public boolean addProduct(Long productId) {
        if (productId != null) {
            if (this.productIds == null) {
                this.productIds = new ArrayList<>();
            }
            return this.productIds.add(productId);
        }
        return false;
    }

    /**
     * @return the total order amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the total order amount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the order status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * @param shippingAddress the shipping address to set
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
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
     * Updates the order status
     * 
     * @param newStatus the new status to set
     * @return true if the status was changed
     */
    public boolean updateStatus(String newStatus) {
        if (this.status != null && this.status.equals(newStatus)) {
            return false;
        }
        this.status = newStatus;
        this.updatedAt = new Date();
        return true;
    }
    
    /**
     * Checks if the order is in a completed state
     * 
     * @return true if the order is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "DELIVERED".equals(status);
    }
    
    /**
     * Checks if the order can be cancelled
     * 
     * @return true if the order can be cancelled
     */
    public boolean isCancellable() {
        return !isCompleted() && !"CANCELLED".equals(status);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", products=" + (productIds != null ? productIds.size() : 0) +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * Builder pattern for Order
     */
    public static class Builder {
        private Long id;
        private Long userId;
        private List<Long> productIds = new ArrayList<>();
        private BigDecimal totalAmount;
        private String status = "CREATED";
        private String shippingAddress;
        private Date createdAt = new Date();
        private Date updatedAt = new Date();
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder productIds(List<Long> productIds) {
            this.productIds = productIds;
            return this;
        }
        
        public Builder addProductId(Long productId) {
            this.productIds.add(productId);
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
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
        
        public Order build() {
            return new Order(id, userId, productIds, totalAmount, status, shippingAddress, createdAt, updatedAt);
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