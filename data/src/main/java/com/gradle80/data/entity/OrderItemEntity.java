package com.gradle80.data.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Order item database entity.
 * 
 * This entity represents an individual item within an order, mapping to a specific product
 * with its quantity and price at the time of order. It extends BaseEntity to inherit
 * common fields (id, createdAt, updatedAt).
 * 
 * The historical price (priceAtOrder) is stored to maintain order integrity even if the
 * product's current price changes over time.
 */
@Entity
@Table(name = "order_items")
public class OrderItemEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "price_at_order", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;
    
    /**
     * Default constructor required by JPA
     */
    public OrderItemEntity() {
        // Required by JPA
    }
    
    /**
     * Constructs a new order item entity with the specified properties.
     *
     * @param order         the parent order
     * @param product       the product reference
     * @param quantity      the item quantity
     * @param priceAtOrder  the price at the time of order
     */
    public OrderItemEntity(OrderEntity order, ProductEntity product, Integer quantity, BigDecimal priceAtOrder) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    /**
     * Convenience constructor that takes the current product price as the order price.
     *
     * @param order         the parent order
     * @param product       the product reference
     * @param quantity      the item quantity
     */
    public OrderItemEntity(OrderEntity order, ProductEntity product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtOrder = product.getPrice();
    }
    
    /**
     * @return the parent order
     */
    public OrderEntity getOrder() {
        return order;
    }
    
    /**
     * @param order the parent order to set
     */
    public void setOrder(OrderEntity order) {
        this.order = order;
    }
    
    /**
     * @return the product reference
     */
    public ProductEntity getProduct() {
        return product;
    }
    
    /**
     * @param product the product reference to set
     */
    public void setProduct(ProductEntity product) {
        this.product = product;
    }
    
    /**
     * @return the item quantity
     */
    public Integer getQuantity() {
        return quantity;
    }
    
    /**
     * @param quantity the item quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    /**
     * @return the price at the time of order
     */
    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }
    
    /**
     * @param priceAtOrder the price at the time of order to set
     */
    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }
    
    /**
     * Calculates the total price for this order item (quantity * price).
     * 
     * @return the total price for this item
     */
    public BigDecimal calculateTotalPrice() {
        return priceAtOrder.multiply(new BigDecimal(quantity));
    }
    
    /**
     * Updates the quantity of this item and ensures it is not negative.
     * 
     * @param newQuantity the new quantity to set
     * @throws IllegalArgumentException if the quantity is negative
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Order item quantity cannot be negative");
        }
        this.quantity = newQuantity;
    }
    
    /**
     * Checks if this order item is for the specified product.
     * 
     * @param productId the product ID to check
     * @return true if this item is for the specified product, false otherwise
     */
    public boolean isForProduct(Long productId) {
        return product != null && product.getId().equals(productId);
    }
    
    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "order=" + (order != null ? order.getId() : "null") +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", priceAtOrder=" + priceAtOrder +
                "} " + super.toString();
    }
}