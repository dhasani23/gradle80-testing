package com.gradle80.data.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Order entity representing an order in the system.
 * This entity maps to the 'orders' table in the database and extends the BaseEntity
 * which provides common fields like id, created_at, and updated_at.
 * 
 * The entity contains order information including the user who placed the order,
 * the total amount, shipping address, and status.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    /**
     * Default constructor required by JPA
     */
    public OrderEntity() {
        // Required by JPA
    }

    /**
     * Constructs a new order entity with the specified properties.
     *
     * @param user            the user who placed the order
     * @param totalAmount     the total order amount
     * @param status          the order status
     * @param shippingAddress the shipping address
     */
    public OrderEntity(UserEntity user, BigDecimal totalAmount, String status, String shippingAddress) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    /**
     * @return the user who placed the order
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the total amount to set
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
     * @param status the status to set
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
     * Updates the order status to the new status
     * 
     * @param newStatus the new status to set
     * @return true if the status was changed, false if it was already at the given status
     */
    public boolean updateStatus(String newStatus) {
        if (this.status.equals(newStatus)) {
            return false;
        }
        
        this.status = newStatus;
        return true;
    }
    
    /**
     * Checks if the order is in a completed state
     * 
     * @return true if the order is completed, false otherwise
     */
    public boolean isCompleted() {
        // FIXME: Add more completed statuses as needed
        return "COMPLETED".equals(status) || "DELIVERED".equals(status);
    }
    
    /**
     * Checks if the order can be cancelled based on its current status
     * 
     * @return true if the order can be cancelled, false otherwise
     */
    public boolean isCancellable() {
        // TODO: Implement business logic for cancellable order states
        return !isCompleted() && !"CANCELLED".equals(status);
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "user=" + (user != null ? user.getUsername() : "null") +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                "} " + super.toString();
    }
}