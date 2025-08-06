package com.gradle80.service.mapper;

import com.gradle80.data.entity.ProductEntity;
import com.gradle80.service.domain.Product;
import com.gradle80.api.dto.ProductDto;

import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ProductEntity, Product domain objects, and DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Maps a ProductEntity to a Product domain object
     *
     * @param entity the product entity to map
     * @return the mapped Product domain object
     */
    public Product fromEntity(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .available(entity.isAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Maps a Product domain object to a ProductEntity
     *
     * @param product the product domain object to map
     * @return the mapped ProductEntity
     */
    public ProductEntity toProductEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setCategory(product.getCategory());
        entity.setAvailable(product.isAvailable());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Updates a ProductEntity with values from a Product domain object
     * 
     * @param product the product domain object with updated values
     * @param entity the product entity to update
     * @return the updated ProductEntity
     */
    public ProductEntity updateProductEntity(Product product, ProductEntity entity) {
        if (product == null || entity == null) {
            return entity;
        }
        
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setCategory(product.getCategory());
        entity.setAvailable(product.isAvailable());
        entity.setUpdatedAt(product.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Maps a Product domain object to a ProductDto
     *
     * @param product the product domain object to map
     * @return the mapped ProductDto
     */
    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setAvailable(product.isAvailable());
        
        return dto;
    }
    
    /**
     * Maps a ProductDto to a Product domain object
     *
     * @param dto the product dto to map
     * @return the mapped Product domain object
     */
    public Product fromDto(ProductDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .available(dto.isAvailable())
                .build();
    }
}