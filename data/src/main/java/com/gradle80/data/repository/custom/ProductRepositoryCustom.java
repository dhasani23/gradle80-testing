package com.gradle80.data.repository.custom;

import com.gradle80.data.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Custom repository methods for Product.
 * This interface defines additional methods not provided by standard Spring Data JPA repositories.
 */
public interface ProductRepositoryCustom {

    /**
     * Find products by applying multiple filters simultaneously.
     * This allows for more complex queries than the standard Spring Data methods.
     * Any filter can be null, in which case it won't be applied.
     *
     * @param name the product name fragment to search for (case-insensitive, can be null)
     * @param category the product category to filter by (can be null)
     * @param minPrice the minimum price threshold (inclusive, can be null)
     * @param maxPrice the maximum price threshold (inclusive, can be null)
     * @return a list of ProductEntity objects matching all the applied filters
     */
    List<ProductEntity> findProductsByFilters(String name, String category, 
                                             BigDecimal minPrice, BigDecimal maxPrice);
}