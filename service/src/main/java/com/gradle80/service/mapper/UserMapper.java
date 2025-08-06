package com.gradle80.service.mapper;

import com.gradle80.data.entity.UserEntity;
import com.gradle80.service.domain.User;
import com.gradle80.api.dto.UserDto;
import com.gradle80.api.request.UserRequest;

import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserEntity, User domain objects, and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Maps a UserEntity to a User domain object
     *
     * @param entity the user entity to map
     * @return the mapped User domain object
     */
    public User fromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Maps a User domain object to a UserEntity
     *
     * @param user the user domain object to map
     * @return the mapped UserEntity
     */
    public UserEntity toUserEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Maps a User domain object to a UserDto
     *
     * @param user the user domain object to map
     * @return the mapped UserDto
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .build();
    }
    
    /**
     * Maps a UserDto to a User domain object
     *
     * @param dto the user dto to map
     * @return the mapped User domain object
     */
    public User fromDto(UserDto dto) {
        if (dto == null) {
            return null;
        }
        
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .active(dto.isActive())
                .build();
    }
    
    /**
     * Maps a UserRequest to a User domain object
     *
     * @param request the user request to map
     * @return the mapped User domain object
     */
    public User fromRequest(UserRequest request) {
        if (request == null) {
            return null;
        }
        
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(true)
                .build();
    }
    
    /**
     * Updates a UserEntity with values from a User domain object
     * 
     * @param user the user domain object with updated values
     * @param entity the user entity to update
     * @return the updated UserEntity
     */
    public UserEntity updateUserEntity(User user, UserEntity entity) {
        if (user == null || entity == null) {
            return entity;
        }
        
        if (user.getUsername() != null) entity.setUsername(user.getUsername());
        if (user.getEmail() != null) entity.setEmail(user.getEmail());
        if (user.getFirstName() != null) entity.setFirstName(user.getFirstName());
        if (user.getLastName() != null) entity.setLastName(user.getLastName());
        entity.setActive(user.isActive());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        return entity;
    }
}