package com.gradle80.service.mapper;

import com.gradle80.api.dto.NotificationDto;
import com.gradle80.data.entity.NotificationEntity;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.service.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting between Notification domain model,
 * NotificationEntity data model, and NotificationDto DTO model.
 * 
 * This class provides bidirectional mapping between the different 
 * representations of a notification across application layers.
 */
@Component
public class NotificationMapper {
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Converts a NotificationDto to a Notification domain object.
     * 
     * @param notificationDto the notification DTO to convert
     * @return the corresponding Notification domain object
     */
    public Notification toEntity(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }
        
        return Notification.builder()
                .id(notificationDto.getId())
                .userId(notificationDto.getUserId())
                .type(notificationDto.getType())
                .message(notificationDto.getMessage())
                .read(notificationDto.isRead())
                .createdAt(notificationDto.getCreatedAt())
                .build();
    }
    
    /**
     * Converts a Notification domain object to a NotificationDto.
     * 
     * @param notification the Notification domain object to convert
     * @return the corresponding NotificationDto
     */
    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    
    /**
     * Converts a NotificationEntity to a Notification domain object.
     * This mapping includes all fields from the entity to the domain object.
     * 
     * @param notificationEntity the notification entity to convert
     * @return the corresponding Notification domain object
     */
    public Notification fromEntity(NotificationEntity notificationEntity) {
        if (notificationEntity == null) {
            return null;
        }
        
        return Notification.builder()
                .id(notificationEntity.getId())
                .userId(notificationEntity.getUser() != null ? notificationEntity.getUser().getId() : null)
                .type(notificationEntity.getType())
                .message(notificationEntity.getMessage())
                .read(notificationEntity.isRead())
                .createdAt(notificationEntity.getCreatedAt())
                .build();
    }
    
    /**
     * Converts a Notification domain object to a NotificationEntity.
     * This mapping includes all fields from the domain object to the entity.
     * 
     * @param notification the Notification domain object to convert
     * @return the corresponding NotificationEntity
     */
    public NotificationEntity toNotificationEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        
        // Set the user reference if userId is provided
        if (notification.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(notification.getUserId())
                    .orElse(null);
            entity.setUser(userEntity);
        }
        
        entity.setType(notification.getType());
        entity.setMessage(notification.getMessage());
        entity.setRead(notification.isRead());
        // Note: Created timestamp is typically managed by JPA
        // Only set it if it needs to be preserved exactly
        
        return entity;
    }
    
    /**
     * Updates an existing NotificationEntity with values from a Notification domain object.
     * This is useful for updating an entity without creating a new instance.
     * 
     * @param notification the Notification domain object with updated values
     * @param notificationEntity the NotificationEntity to update
     */
    public void updateNotificationEntity(Notification notification, NotificationEntity notificationEntity) {
        if (notification == null || notificationEntity == null) {
            return;
        }
        
        // Only update fields that are present in the Notification object
        if (notification.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(notification.getUserId())
                    .orElse(null);
            notificationEntity.setUser(userEntity);
        }
        
        if (notification.getType() != null) {
            notificationEntity.setType(notification.getType());
        }
        
        if (notification.getMessage() != null) {
            notificationEntity.setMessage(notification.getMessage());
        }
        
        notificationEntity.setRead(notification.isRead());
    }
    
    /**
     * Directly converts a NotificationEntity to a NotificationDto without the intermediary Notification domain object.
     * This can be more efficient when the domain model is not needed.
     * 
     * @param notificationEntity the notification entity to convert
     * @return the corresponding NotificationDto
     */
    public NotificationDto entityToDto(NotificationEntity notificationEntity) {
        if (notificationEntity == null) {
            return null;
        }
        
        return NotificationDto.builder()
                .id(notificationEntity.getId())
                .userId(notificationEntity.getUser() != null ? notificationEntity.getUser().getId() : null)
                .type(notificationEntity.getType())
                .message(notificationEntity.getMessage())
                .read(notificationEntity.isRead())
                .createdAt(notificationEntity.getCreatedAt())
                .build();
    }
    
    /**
     * Directly converts a NotificationDto to a NotificationEntity without the intermediary Notification domain object.
     * 
     * @param notificationDto the notification DTO to convert
     * @return the corresponding NotificationEntity
     */
    public NotificationEntity dtoToEntity(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }
        
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notificationDto.getId());
        
        // Set the user reference if userId is provided
        if (notificationDto.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(notificationDto.getUserId())
                    .orElse(null);
            entity.setUser(userEntity);
        }
        
        entity.setType(notificationDto.getType());
        entity.setMessage(notificationDto.getMessage());
        entity.setRead(notificationDto.isRead());
        // Note: Created timestamp is typically managed by JPA
        
        return entity;
    }
    
    // TODO: Add batch conversion methods for collections of notifications
}