package com.gradle80.data.repository;

import com.gradle80.data.entity.OrderEntity;
import com.gradle80.data.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for order operations.
 * Provides methods for searching and filtering orders in the database.
 * Extends the CustomJpaRepository to inherit common repository functionality.
 */
@Repository
public interface OrderRepository extends CustomJpaRepository<OrderEntity, Long> {

    /**
     * Find all orders for a specific user.
     * 
     * @param user the user whose orders to retrieve
     * @return a list of OrderEntity objects for the given user
     */
    List<OrderEntity> findByUser(UserEntity user);
    
    /**
     * Find all orders with the specified status.
     * 
     * @param status the order status to search for
     * @return a list of OrderEntity objects matching the status
     */
    List<OrderEntity> findByStatus(String status);
    
    /**
     * Count the number of orders for a specific user.
     * 
     * @param user the user whose orders to count
     * @return the count of orders for the given user
     */
    int countByUser(UserEntity user);
    
    /**
     * Find orders created between the specified date range.
     * 
     * @param startDate the starting date (inclusive)
     * @param endDate the ending date (inclusive)
     * @return a list of OrderEntity objects created within the date range
     */
    List<OrderEntity> findByCreatedAtBetween(Date startDate, Date endDate);
    
    /**
     * Find orders by user and status.
     * 
     * @param user the user whose orders to retrieve
     * @param status the order status to filter by
     * @return a list of OrderEntity objects for the given user with the specified status
     */
    List<OrderEntity> findByUserAndStatus(UserEntity user, String status);
    
    /**
     * Find orders by status and created after a specific date.
     * 
     * @param status the order status to filter by
     * @param date the date to compare against
     * @return a list of OrderEntity objects with the specified status created after the given date
     */
    List<OrderEntity> findByStatusAndCreatedAtAfter(String status, Date date);
    
    /**
     * Count orders by status.
     * 
     * @param status the order status to count
     * @return the count of orders with the specified status
     */
    int countByStatus(String status);
    
    // TODO: Add method for complex order filtering with multiple criteria
    
    // FIXME: Consider optimizing queries with large result sets for better performance
}