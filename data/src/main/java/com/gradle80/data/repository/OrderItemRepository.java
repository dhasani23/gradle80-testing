package com.gradle80.data.repository;

import com.gradle80.data.entity.OrderEntity;
import com.gradle80.data.entity.OrderItemEntity;
import com.gradle80.data.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for order item operations.
 * Handles database operations for OrderItemEntity instances such as:
 * - Finding items by their associated order
 * - Finding items by product
 * - Deleting items by order
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    /**
     * Find all order items associated with a specific order.
     *
     * @param order The order entity to find items for
     * @return List of order items belonging to the order
     */
    List<OrderItemEntity> findByOrder(OrderEntity order);
    
    /**
     * Find all order items that contain a specific product.
     * This can be used to track product usage across orders.
     *
     * @param product The product entity to search for
     * @return List of order items containing the specified product
     */
    List<OrderItemEntity> findByProduct(ProductEntity product);
    
    /**
     * Delete all order items associated with a specific order.
     * This is typically used when canceling or deleting an order.
     * 
     * @param order The order entity whose items should be deleted
     */
    void deleteByOrder(OrderEntity order);
    
    // TODO: Consider adding method to find order items by quantity threshold
    
    // FIXME: Performance optimization needed for bulk operations on order items
}