package com.gradle80.service.implementation;

import com.gradle80.api.dto.ProductDto;
import com.gradle80.api.request.ProductRequest;
import com.gradle80.api.request.SearchRequest;
import com.gradle80.api.response.ProductResponse;
import com.gradle80.api.response.SearchResponse;
import com.gradle80.api.service.ProductService;
import com.gradle80.api.service.SearchService;
import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.repository.ProductRepository;
import com.gradle80.service.cache.CacheManager;
import com.gradle80.service.domain.Product;
import com.gradle80.service.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Implementation of product management service.
 * Provides core business logic for product operations including
 * creation, retrieval, update, deletion, and search.
 *
 * @since 1.0
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";
    private static final long PRODUCT_CACHE_TTL = 3600; // 1 hour in seconds

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;
    private final SearchService searchService;

    /**
     * Constructor for dependency injection.
     *
     * @param productRepository repository for product data access
     * @param productMapper mapper for product domain/DTO conversion
     * @param cacheManager cache manager for product caching
     * @param searchService service for product searching
     */
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                             ProductMapper productMapper,
                             CacheManager cacheManager,
                             SearchService searchService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.cacheManager = cacheManager;
        this.searchService = searchService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        logger.debug("Retrieving product with ID: {}", productId);
        
        if (productId == null) {
            logger.error("Product ID cannot be null");
            return ProductResponse.error("Product ID cannot be null");
        }

        // Try to get from cache first
        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
        ProductDto cachedProduct = cacheManager.get(cacheKey, ProductDto.class);
        
        if (cachedProduct != null) {
            logger.debug("Product with ID {} found in cache", productId);
            return ProductResponse.success("Product retrieved successfully", cachedProduct);
        }

        // If not in cache, get from repository
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if (productEntity == null) {
            logger.warn("Product with ID {} not found", productId);
            return ProductResponse.error("Product not found");
        }

        // Convert entity to domain model to DTO
        Product product = productMapper.fromEntity(productEntity);
        ProductDto productDto = productMapper.toDto(product);
        
        // Cache the result
        cacheManager.put(cacheKey, productDto, PRODUCT_CACHE_TTL);
        
        logger.debug("Product with ID {} retrieved successfully", productId);
        return ProductResponse.success("Product retrieved successfully", productDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        logger.debug("Creating new product");
        
        if (request == null) {
            logger.error("Product request cannot be null");
            return ProductResponse.error("Product request cannot be null");
        }
        
        if (!request.validate()) {
            logger.error("Invalid product request");
            return ProductResponse.error("Invalid product request");
        }

        try {
            // Convert request to domain model
            ProductDto requestDto = new ProductDto();
            requestDto.setName(request.getName());
            requestDto.setDescription(request.getDescription());
            requestDto.setPrice(request.getPrice());
            requestDto.setCategory(request.getCategory());
            requestDto.setAvailable(true);
            
            Product product = productMapper.fromDto(requestDto);
            
            // Set creation timestamps
            Date now = new Date();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            // Convert domain model to entity and save
            ProductEntity productEntity = productMapper.toProductEntity(product);
            productEntity = productRepository.save(productEntity);
            
            // Convert saved entity back to domain model and DTO
            product = productMapper.fromEntity(productEntity);
            ProductDto productDto = productMapper.toDto(product);
            
            logger.info("Product created successfully with ID: {}", productDto.getId());
            return ProductResponse.success("Product created successfully", productDto);
        } catch (Exception e) {
            logger.error("Error creating product", e);
            return ProductResponse.error("Error creating product: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        logger.debug("Updating product with ID: {}", productId);
        
        if (productId == null) {
            logger.error("Product ID cannot be null");
            return ProductResponse.error("Product ID cannot be null");
        }
        
        if (request == null) {
            logger.error("Product request cannot be null");
            return ProductResponse.error("Product request cannot be null");
        }
        
        if (!request.validate()) {
            logger.error("Invalid product request");
            return ProductResponse.error("Invalid product request");
        }

        try {
            // Find existing product
            ProductEntity existingProductEntity = productRepository.findById(productId).orElse(null);
            if (existingProductEntity == null) {
                logger.warn("Product with ID {} not found for update", productId);
                return ProductResponse.error("Product not found");
            }
            
            // Convert request to domain model
            ProductDto requestDto = new ProductDto();
            requestDto.setName(request.getName());
            requestDto.setDescription(request.getDescription());
            requestDto.setPrice(request.getPrice());
            requestDto.setCategory(request.getCategory());
            
            // Keep the original availability status
            requestDto.setAvailable(existingProductEntity.isAvailable());
            
            Product product = productMapper.fromDto(requestDto);
            product.setId(productId);
            product.setUpdatedAt(new Date());
            
            // Update entity with values from domain model
            productMapper.updateProductEntity(product, existingProductEntity);
            
            // Save updated entity
            existingProductEntity = productRepository.save(existingProductEntity);
            
            // Convert updated entity to DTO
            product = productMapper.fromEntity(existingProductEntity);
            ProductDto productDto = productMapper.toDto(product);
            
            // Update cache with the new data
            String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
            cacheManager.evict(cacheKey);
            cacheManager.put(cacheKey, productDto, PRODUCT_CACHE_TTL);
            
            logger.info("Product with ID {} updated successfully", productId);
            return ProductResponse.success("Product updated successfully", productDto);
        } catch (Exception e) {
            logger.error("Error updating product with ID: {}", productId, e);
            return ProductResponse.error("Error updating product: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse deleteProduct(Long productId) {
        logger.debug("Deleting product with ID: {}", productId);
        
        if (productId == null) {
            logger.error("Product ID cannot be null");
            return ProductResponse.error("Product ID cannot be null");
        }

        try {
            // Check if product exists
            ProductEntity productEntity = productRepository.findById(productId).orElse(null);
            if (productEntity == null) {
                logger.warn("Product with ID {} not found for deletion", productId);
                return ProductResponse.error("Product not found");
            }
            
            // Convert to domain model and DTO for response before deletion
            Product product = productMapper.fromEntity(productEntity);
            ProductDto productDto = productMapper.toDto(product);
            
            // Delete from repository
            productRepository.deleteById(productId);
            
            // Remove from cache
            String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
            cacheManager.evict(cacheKey);
            
            logger.info("Product with ID {} deleted successfully", productId);
            return ProductResponse.success("Product deleted successfully", productDto);
        } catch (Exception e) {
            logger.error("Error deleting product with ID: {}", productId, e);
            return ProductResponse.error("Error deleting product: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SearchResponse searchProducts(SearchRequest request) {
        logger.debug("Searching products with request: {}", request);
        
        if (request == null) {
            logger.error("Search request cannot be null");
            return new SearchResponse(false, "Search request cannot be null");
        }
        
        if (!request.validate()) {
            logger.error("Invalid search request");
            return new SearchResponse(false, "Invalid search request");
        }

        try {
            // Delegate to search service for complex product searches
            return searchService.search(request);
        } catch (Exception e) {
            logger.error("Error searching products", e);
            return new SearchResponse(false, "Error searching products: " + e.getMessage());
        }
    }
    
    /**
     * Marks a product as unavailable without deleting it.
     * This is useful when a product is temporarily out of stock.
     *
     * @param productId the ID of the product to mark as unavailable
     * @return a ProductResponse indicating success or failure
     */
    @Transactional
    public ProductResponse markProductUnavailable(Long productId) {
        logger.debug("Marking product with ID {} as unavailable", productId);
        
        if (productId == null) {
            return ProductResponse.error("Product ID cannot be null");
        }
        
        try {
            ProductEntity productEntity = productRepository.findById(productId).orElse(null);
            if (productEntity == null) {
                return ProductResponse.error("Product not found");
            }
            
            productEntity.setAvailable(false);
            productEntity = productRepository.save(productEntity);
            
            // Update cache
            Product product = productMapper.fromEntity(productEntity);
            ProductDto productDto = productMapper.toDto(product);
            
            String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
            cacheManager.evict(cacheKey);
            cacheManager.put(cacheKey, productDto, PRODUCT_CACHE_TTL);
            
            logger.info("Product with ID {} marked as unavailable", productId);
            return ProductResponse.success("Product marked as unavailable", productDto);
        } catch (Exception e) {
            logger.error("Error marking product as unavailable", e);
            return ProductResponse.error("Error marking product as unavailable: " + e.getMessage());
        }
    }
    
    /**
     * Marks a product as available for purchase.
     *
     * @param productId the ID of the product to mark as available
     * @return a ProductResponse indicating success or failure
     */
    @Transactional
    public ProductResponse markProductAvailable(Long productId) {
        logger.debug("Marking product with ID {} as available", productId);
        
        if (productId == null) {
            return ProductResponse.error("Product ID cannot be null");
        }
        
        try {
            ProductEntity productEntity = productRepository.findById(productId).orElse(null);
            if (productEntity == null) {
                return ProductResponse.error("Product not found");
            }
            
            productEntity.setAvailable(true);
            productEntity = productRepository.save(productEntity);
            
            // Update cache
            Product product = productMapper.fromEntity(productEntity);
            ProductDto productDto = productMapper.toDto(product);
            
            String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
            cacheManager.evict(cacheKey);
            cacheManager.put(cacheKey, productDto, PRODUCT_CACHE_TTL);
            
            logger.info("Product with ID {} marked as available", productId);
            return ProductResponse.success("Product marked as available", productDto);
        } catch (Exception e) {
            logger.error("Error marking product as available", e);
            return ProductResponse.error("Error marking product as available: " + e.getMessage());
        }
    }
    
    // TODO: Add batch operations for product management
    // TODO: Implement inventory tracking functionality
    // TODO: Add methods for product categorization and tagging
    
    // FIXME: Cache invalidation strategy should be improved for better performance
    // FIXME: Consider implementing optimistic locking to prevent concurrent update issues
}