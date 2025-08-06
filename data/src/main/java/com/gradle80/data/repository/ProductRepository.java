package com.gradle80.data.repository;

import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.repository.custom.ProductRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for product operations.
 * Provides methods for searching and filtering products in the database.
 * Extends the CustomJpaRepository to inherit common repository functionality.
 */
@Repository
public interface ProductRepository extends CustomJpaRepository<ProductEntity, Long>, ProductRepositoryCustom {

    /**
     * Find products by name, performing a case-insensitive partial match.
     * This method allows searching for products with names containing the given string.
     * 
     * @param name the name fragment to search for (case-insensitive)
     * @return a list of ProductEntity objects whose names contain the search string
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products by their category.
     * 
     * @param category the category to filter by
     * @return a list of ProductEntity objects in the specified category
     */
    List<ProductEntity> findByCategory(String category);
    
    /**
     * Find all products with the specified availability status.
     * 
     * @param available true for available products, false for unavailable products
     * @return a list of ProductEntity objects matching the availability status
     */
    List<ProductEntity> findAllByAvailable(boolean available);
    
    /**
     * Find products with a price less than the specified amount.
     * 
     * @param price the maximum price (exclusive)
     * @return a list of ProductEntity objects with prices less than the specified amount
     */
    List<ProductEntity> findByPriceLessThan(BigDecimal price);
    
    /**
     * Find products with a price greater than or equal to the specified amount.
     * 
     * @param price the minimum price (inclusive)
     * @return a list of ProductEntity objects with prices greater than or equal to the specified amount
     */
    List<ProductEntity> findByPriceGreaterThanEqual(BigDecimal price);
    
    /**
     * Find products with prices between the specified range (inclusive).
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return a list of ProductEntity objects with prices in the specified range
     */
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Count products in the specified category.
     * 
     * @param category the category to count products for
     * @return the count of products in the specified category
     */
    long countByCategory(String category);
    
    /**
     * Find products by category that are available.
     * 
     * @param category the category to filter by
     * @param available the availability status (typically true)
     * @return a list of available products in the specified category
     */
    List<ProductEntity> findByCategoryAndAvailable(String category, boolean available);
    
    // TODO: Add method to find featured products once that property is added to the entity
    
    // TODO: Add method for advanced product search with multiple criteria
}