package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order creation request.
 * This class encapsulates the data required to create a new order in the system.
 */
public class OrderRequest extends ApiRequest {
    
    /**
     * User identifier who is placing the order
     */
    private Long userId;
    
    /**
     * List of product identifiers to be included in the order
     */
    private List<Long> productIds;
    
    /**
     * Address where the order should be shipped
     */
    private String shippingAddress;

    /**
     * Default constructor
     */
    public OrderRequest() {
        super();
    }

    /**
     * Constructor with all fields
     *
     * @param userId          the user identifier
     * @param productIds      list of product identifiers
     * @param shippingAddress the shipping address
     */
    public OrderRequest(Long userId, List<Long> productIds, String shippingAddress) {
        super();
        this.userId = userId;
        this.productIds = productIds;
        this.shippingAddress = shippingAddress;
    }

    /**
     * Gets the user identifier
     *
     * @return the user id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier
     *
     * @param userId the user id to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the list of product identifiers
     *
     * @return the list of product ids
     */
    public List<Long> getProductIds() {
        return productIds != null ? productIds : Collections.emptyList();
    }

    /**
     * Sets the list of product identifiers
     *
     * @param productIds the product ids to set
     */
    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    /**
     * Gets the shipping address
     *
     * @return the shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the shipping address
     *
     * @param shippingAddress the shipping address to set
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Validates the order request.
     * Checks if all required fields are present and valid.
     *
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First validate base fields from parent class
        if (!super.validate()) {
            return false;
        }
        
        // Validate user ID exists
        if (userId == null || userId <= 0) {
            return false;
        }
        
        // Validate product IDs exist and the list is not empty
        if (productIds == null || productIds.isEmpty()) {
            return false;
        }
        
        // Check if any product ID is invalid
        for (Long productId : productIds) {
            if (productId == null || productId <= 0) {
                return false;
            }
        }
        
        // Validate shipping address is provided
        return shippingAddress != null && !shippingAddress.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderRequest that = (OrderRequest) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(productIds, that.productIds) &&
               Objects.equals(shippingAddress, that.shippingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, productIds, shippingAddress);
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "requestId='" + getRequestId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", userId=" + userId +
                ", productIds=" + productIds +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}