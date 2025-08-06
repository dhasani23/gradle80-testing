package com.gradle80.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gradle80.api.dto.OrderDto;
import com.gradle80.api.dto.ProductDto;
import com.gradle80.data.entity.OrderEntity;
import com.gradle80.data.entity.OrderItemEntity;
import com.gradle80.data.entity.ProductEntity;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.OrderItemRepository;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.service.domain.Product;

/**
 * Mapper class responsible for transforming between Order entity and DTO objects.
 * Handles the conversion of Order data between persistence layer and service layer.
 */
@Component
public class OrderMapper {
    
    private final ProductMapper productMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    /**
     * Constructor with required dependencies
     * 
     * @param productMapper mapper for product entities and DTOs
     */
    @Autowired
    public OrderMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    
    /**
     * Converts an OrderDto to an OrderEntity
     * 
     * @param orderDto the data transfer object to convert
     * @return the corresponding entity object
     */
    public OrderEntity toEntity(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }
        
        OrderEntity entity = new OrderEntity();
        
        // Set basic properties
        entity.setId(orderDto.getId());
        entity.setTotalAmount(orderDto.getTotalAmount());
        entity.setStatus(orderDto.getStatus());
        
        // Set user if userId is provided
        if (orderDto.getUserId() != null) {
            UserEntity user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + orderDto.getUserId()));
            entity.setUser(user);
        }
        
        // Set timestamps if available
        if (orderDto.getCreatedAt() != null) {
            entity.setCreatedAt(orderDto.getCreatedAt());
        }
        
        // Note: Order items need to be handled separately after order is persisted
        // as they require a valid order reference
        
        return entity;
    }
    
    /**
     * Converts an OrderEntity to an OrderDto
     * 
     * @param order the entity to convert
     * @return the corresponding data transfer object
     */
    public OrderDto toDto(OrderEntity order) {
        if (order == null) {
            return null;
        }
        
        OrderDto dto = new OrderDto();
        
        // Set basic properties
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        
        // Set user ID
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }
        
        // Convert order items to product DTOs
        // Fetch order items for this order
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrder(order);
        if (orderItems != null && !orderItems.isEmpty()) {
            List<ProductDto> products = orderItems.stream()
                .map(item -> {
                    ProductEntity productEntity = item.getProduct();
                    Product product = productMapper.fromEntity(productEntity);
                    ProductDto productDto = productMapper.toDto(product);
                    // TODO: Add quantity information from order item to product DTO
                    return productDto;
                })
                .collect(Collectors.toList());
            
            dto.setProducts(products);
        } else {
            dto.setProducts(new ArrayList<>());
        }
        
        return dto;
    }
    
    /**
     * Converts a list of OrderEntity objects to a list of OrderDto objects
     * 
     * @param orders the entities to convert
     * @return list of data transfer objects
     */
    public List<OrderDto> toDtoList(List<OrderEntity> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Updates an existing OrderEntity from an OrderDto
     * 
     * @param orderDto the data transfer object with updated data
     * @param existingOrder the entity to update
     * @return the updated entity
     */
    public OrderEntity updateEntityFromDto(OrderDto orderDto, OrderEntity existingOrder) {
        if (orderDto == null || existingOrder == null) {
            return existingOrder;
        }
        
        // Update mutable properties
        if (orderDto.getStatus() != null) {
            existingOrder.setStatus(orderDto.getStatus());
        }
        
        if (orderDto.getTotalAmount() != null) {
            existingOrder.setTotalAmount(orderDto.getTotalAmount());
        }
        
        // FIXME: Updating order items requires special handling
        // as we need to determine which items to add, update, or delete
        
        return existingOrder;
    }
}