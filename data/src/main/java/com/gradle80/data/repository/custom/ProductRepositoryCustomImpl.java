package com.gradle80.data.repository.custom;

import com.gradle80.data.entity.ProductEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of custom Product repository methods.
 * This class provides implementations for the methods defined in ProductRepositoryCustom
 * using JPA Criteria API for flexible and dynamic query construction.
 */
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(ProductRepositoryCustomImpl.class.getName());
    private static final int MAX_RESULTS_LIMIT = 500;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     * 
     * This implementation uses JPA Criteria API to build a dynamic query based on the provided filters.
     * The method handles null values for any filter parameter, in which case that filter is not applied.
     * Results are ordered by name by default and limited to prevent excessive data retrieval.
     */
    @Override
    public List<ProductEntity> findProductsByFilters(String name, String category, 
                                                    BigDecimal minPrice, BigDecimal maxPrice) {
        LOGGER.log(Level.FINE, "Finding products with filters: name={0}, category={1}, minPrice={2}, maxPrice={3}", 
                new Object[]{name, category, minPrice, maxPrice});
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = query.from(ProductEntity.class);
        
        List<Predicate> predicates = buildPredicates(cb, product, name, category, minPrice, maxPrice);
        List<Order> orders = buildOrderCriteria(cb, product);
        
        // Apply predicates if any exist
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        
        // Apply ordering
        query.orderBy(orders);
        
        // Create and configure the query
        TypedQuery<ProductEntity> typedQuery = entityManager.createQuery(query);
        
        // Apply result limit to avoid excessive data retrieval
        typedQuery.setMaxResults(MAX_RESULTS_LIMIT);
        
        // Execute query and return results
        List<ProductEntity> results = typedQuery.getResultList();
        LOGGER.log(Level.FINE, "Found {0} products matching the criteria", results.size());
        
        return results;
    }
    
    /**
     * Builds the list of predicates based on the provided filter criteria.
     * 
     * @param cb the criteria builder
     * @param product the root entity
     * @param name the product name filter
     * @param category the product category filter
     * @param minPrice the minimum price filter
     * @param maxPrice the maximum price filter
     * @return list of predicates
     */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<ProductEntity> product,
                                          String name, String category, 
                                          BigDecimal minPrice, BigDecimal maxPrice) {
        List<Predicate> predicates = new ArrayList<>();
        
        // Add name predicate if provided (case-insensitive partial match)
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(product.get("name")), "%" + name.toLowerCase() + "%"));
        }
        
        // Add category predicate if provided (exact match)
        if (category != null && !category.trim().isEmpty()) {
            // For case insensitive category matching, use:
            predicates.add(cb.equal(cb.lower(product.get("category")), category.toLowerCase()));
        }
        
        // Add price range predicates if provided
        addPricePredicates(cb, product, minPrice, maxPrice, predicates);
        
        // Always filter to only include available products
        // FIXME: Make availability filtering configurable via a parameter
        predicates.add(cb.equal(product.get("available"), true));
        
        return predicates;
    }
    
    /**
     * Adds price-related predicates to the predicate list.
     * 
     * @param cb the criteria builder
     * @param product the root entity
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param predicates the list of predicates to add to
     */
    private void addPricePredicates(CriteriaBuilder cb, Root<ProductEntity> product,
                                  BigDecimal minPrice, BigDecimal maxPrice, 
                                  List<Predicate> predicates) {
        if (minPrice != null) {
            if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
                LOGGER.warning("Minimum price filter was negative. Setting to zero.");
                minPrice = BigDecimal.ZERO;
            }
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            if (maxPrice.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.warning("Maximum price filter was negative or zero. Ignoring this filter.");
            } else {
                predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
                
                // Add validation to ensure min price doesn't exceed max price
                if (minPrice != null && minPrice.compareTo(maxPrice) > 0) {
                    LOGGER.warning("Minimum price exceeds maximum price. This may result in no products found.");
                    // TODO: Consider throwing an exception or handling this case differently
                }
            }
        }
    }
    
    /**
     * Builds the order criteria for the query.
     * 
     * @param cb the criteria builder
     * @param product the root entity
     * @return list of order criteria
     */
    private List<Order> buildOrderCriteria(CriteriaBuilder cb, Root<ProductEntity> product) {
        List<Order> orders = new ArrayList<>();
        
        // Primary sort by category
        orders.add(cb.asc(product.get("category")));
        
        // Secondary sort by name
        orders.add(cb.asc(product.get("name")));
        
        return orders;
    }
}