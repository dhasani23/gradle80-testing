package com.gradle80.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Data transfer object for order information.
 * This class represents an order in the system and contains all relevant
 * information about the order, including the products ordered,
 * total amount, and status.
 */
public class OrderDto {
    
    private Long id;
    private Long userId;
    private List<ProductDto> products;
    private BigDecimal totalAmount;
    private String status;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public OrderDto() {
        this.products = new ArrayList<>();
        this.createdAt = new Date();
    }
    
    /**
     * Parameterized constructor with essential fields
     * 
     * @param id Order identifier
     * @param userId User identifier
     * @param status Order status
     */
    public OrderDto(Long id, Long userId, String status) {
        this();
        this.id = id;
        this.userId = userId;
        this.status = status;
    }
    
    /**
     * Fully parameterized constructor
     * 
     * @param id Order identifier
     * @param userId User identifier
     * @param products List of products in the order
     * @param totalAmount Total order amount
     * @param status Order status
     * @param createdAt Creation timestamp
     */
    public OrderDto(Long id, Long userId, List<ProductDto> products, 
                    BigDecimal totalAmount, String status, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.products = products != null ? products : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt != null ? createdAt : new Date();
    }
    
    /**
     * Get the order identifier
     * 
     * @return the order id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Set the order identifier
     * 
     * @param id the order id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Get the user identifier
     * 
     * @return the user id
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Set the user identifier
     * 
     * @param userId the user id to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Get the products in the order
     * 
     * @return list of products
     */
    public List<ProductDto> getProducts() {
        return products;
    }
    
    /**
     * Set the products in the order
     * 
     * @param products the products to set
     */
    public void setProducts(List<ProductDto> products) {
        this.products = products != null ? products : new ArrayList<>();
    }
    
    /**
     * Add a product to the order
     * 
     * @param product the product to add
     * @return true if product was added
     */
    public boolean addProduct(ProductDto product) {
        if (product != null) {
            if (this.products == null) {
                this.products = new ArrayList<>();
            }
            return this.products.add(product);
        }
        return false;
    }
    
    /**
     * Get the total order amount
     * 
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * Set the total order amount
     * 
     * @param totalAmount the total amount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    /**
     * Get the order status
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Set the order status
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Get the creation timestamp
     * 
     * @return the creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Set the creation timestamp
     * 
     * @param createdAt the creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt != null ? createdAt : new Date();
    }
    
    /**
     * Calculate the total amount based on the products in the order
     * 
     * TODO: Implement price calculation logic considering possible discounts
     */
    public void calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (products != null) {
            for (ProductDto product : products) {
                if (product != null && product.getPrice() != null) {
                    total = total.add(product.getPrice());
                }
                // FIXME: This calculation doesn't account for product quantities
            }
        }
        this.totalAmount = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id) &&
               Objects.equals(userId, orderDto.userId) &&
               Objects.equals(status, orderDto.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, status);
    }

    @Override
    public String toString() {
        return "OrderDto{" +
               "id=" + id +
               ", userId=" + userId +
               ", products.size=" + (products != null ? products.size() : 0) +
               ", totalAmount=" + totalAmount +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}