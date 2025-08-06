package com.gradle80.service.implementation;

import com.gradle80.api.request.UserRequest;
import com.gradle80.api.response.UserResponse;
import com.gradle80.api.dto.UserDto;
import com.gradle80.api.service.UserService;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.data.exception.DataModuleException;
import com.gradle80.service.mapper.UserMapper;
import com.gradle80.service.domain.User;
import com.gradle80.service.event.UserCreatedEvent;
import com.gradle80.service.event.UserDeletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Implementation of UserService interface providing user management operations
 * including creation, retrieval, update and deletion of users.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructs a new UserServiceImpl with required dependencies
     *
     * @param userRepository repository for user data access
     * @param userMapper mapper for converting between entities and DTOs
     * @param eventPublisher publisher for domain events
     */
    @Autowired
    public UserServiceImpl(
            UserRepository userRepository, 
            UserMapper userMapper,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Retrieves a user by their unique identifier
     *
     * @param userId the user's unique identifier
     * @return UserResponse containing user details
     * @throws DataModuleException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        logger.debug("Retrieving user with ID: {}", userId);
        
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new DataModuleException("User not found with ID: " + userId));
        
        User user = userMapper.fromEntity(userEntity);
        UserDto userDto = userMapper.toDto(user);
        
        UserResponse response = new UserResponse();
        response.setSuccess(true);
        response.setMessage("User retrieved successfully");
        response.setUser(userDto);
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }

    /**
     * Creates a new user based on the provided request
     *
     * @param request containing user details
     * @return UserResponse with created user details
     * @throws DataModuleException if username or email already exists
     */
    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        logger.debug("Creating new user with username: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new DataModuleException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new DataModuleException("Email already exists: " + request.getEmail());
        }
        
        // Convert request to domain model
        User user = userMapper.fromRequest(request);
        
        // Set default values
        user.setActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        // Convert domain model to entity
        UserEntity userEntity = userMapper.toUserEntity(user);
        
        // TODO: Implement password hashing before storing
        userEntity.setPasswordHash(request.getPassword()); // This is insecure, needs proper hashing
        
        // Save user
        userEntity = userRepository.save(userEntity);
        
        // Create response
        user = userMapper.fromEntity(userEntity);
        UserDto userDto = userMapper.toDto(user);
        
        UserResponse response = new UserResponse();
        response.setSuccess(true);
        response.setMessage("User created successfully");
        response.setUser(userDto);
        response.setTimestamp(System.currentTimeMillis());
        
        // Publish user created event
        eventPublisher.publishEvent(new UserCreatedEvent(
                userEntity.getId(),
                userEntity.getUsername(),
                System.currentTimeMillis()
        ));
        
        return response;
    }

    /**
     * Updates an existing user based on the provided request
     *
     * @param userId the user's unique identifier
     * @param request containing updated user details
     * @return UserResponse with updated user details
     * @throws DataModuleException if user not found or validation fails
     */
    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        logger.debug("Updating user with ID: {}", userId);
        
        // Find existing user
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataModuleException("User not found with ID: " + userId));
        
        // Check username uniqueness if changed
        if (!existingUser.getUsername().equals(request.getUsername())) {
            UserEntity userWithSameUsername = userRepository.findByUsername(request.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getId().equals(userId)) {
                throw new DataModuleException("Username already exists: " + request.getUsername());
            }
        }
        
        // Check email uniqueness if changed
        if (!existingUser.getEmail().equals(request.getEmail())) {
            UserEntity userWithSameEmail = userRepository.findByEmail(request.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(userId)) {
                throw new DataModuleException("Email already exists: " + request.getEmail());
            }
        }
        
        // Convert to domain model, update fields, and convert back
        User user = userMapper.fromEntity(existingUser);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUpdatedAt(new Date());
        
        userMapper.updateUserEntity(user, existingUser);
        
        // FIXME: Password update should involve proper hashing and validation
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPasswordHash(request.getPassword()); // This is insecure, needs proper hashing
        }
        
        // Save updated user
        existingUser = userRepository.save(existingUser);
        
        // Create response
        user = userMapper.fromEntity(existingUser);
        UserDto userDto = userMapper.toDto(user);
        
        UserResponse response = new UserResponse();
        response.setSuccess(true);
        response.setMessage("User updated successfully");
        response.setUser(userDto);
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }

    /**
     * Deletes (or deactivates) a user by their unique identifier
     *
     * @param userId the user's unique identifier
     * @return UserResponse with deactivated user details
     * @throws DataModuleException if user not found
     */
    @Override
    @Transactional
    public UserResponse deleteUser(Long userId) {
        logger.debug("Deleting user with ID: {}", userId);
        
        // Find existing user
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataModuleException("User not found with ID: " + userId));
        
        // Soft delete - mark as inactive instead of removing from database
        existingUser.setActive(false);
        existingUser.setUpdatedAt(new Date());
        
        userRepository.save(existingUser);
        
        // Create response
        User user = userMapper.fromEntity(existingUser);
        UserDto userDto = userMapper.toDto(user);
        
        UserResponse response = new UserResponse();
        response.setSuccess(true);
        response.setMessage("User deleted successfully");
        response.setUser(userDto);
        response.setTimestamp(System.currentTimeMillis());
        
        // Publish user deleted event
        eventPublisher.publishEvent(new UserDeletedEvent(
                existingUser.getId(),
                System.currentTimeMillis()
        ));
        
        return response;
    }
}