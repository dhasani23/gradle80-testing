package com.gradle80.web.controller;

import com.gradle80.api.request.UserRequest;
import com.gradle80.api.response.UserResponse;
import com.gradle80.api.service.UserService;
import com.gradle80.web.filter.ResponseEnhancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

/**
 * REST controller for user operations.
 * Provides endpoints for creating, retrieving, updating, and deleting user resources.
 */
@RestController
@RequestMapping("/api/users")
@Api(value = "User Management", description = "Operations for user management")
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final ResponseEnhancer responseEnhancer;

    /**
     * Constructor for dependency injection.
     *
     * @param userService UserService for handling user-related operations
     * @param responseEnhancer ResponseEnhancer for enriching API responses
     */
    @Autowired
    public UserController(UserService userService, ResponseEnhancer responseEnhancer) {
        this.userService = userService;
        this.responseEnhancer = responseEnhancer;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return ResponseEntity containing the user information or an error response
     */
    @GetMapping("/{userId}")
    @ApiOperation(value = "Get user by ID", notes = "Returns a user based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved user"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable 
            @ApiParam(value = "User ID", required = true) 
            Long userId) {
        log.debug("REST request to get User with id: {}", userId);
        
        try {
            UserResponse response = userService.getUserById(userId);
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving user with ID {}: {}", userId, e.getMessage());
            // Let the global exception handler deal with the exception
            throw e;
        }
    }

    /**
     * Creates a new user.
     *
     * @param request the user creation request containing user details
     * @return ResponseEntity containing the created user information or an error response
     */
    @PostMapping
    @ApiOperation(value = "Create a new user", notes = "Creates a new user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "User successfully created"),
        @ApiResponse(code = 400, message = "Invalid input data"),
        @ApiResponse(code = 409, message = "User already exists"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody 
            @ApiParam(value = "User details", required = true) 
            UserRequest request) {
        log.debug("REST request to create User: {}", request);
        
        try {
            // FIXME: Add additional validation for duplicate username/email
            UserResponse response = userService.createUser(request);
            return responseEnhancer.enhance(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update
     * @param request the user update request containing updated user details
     * @return ResponseEntity containing the updated user information or an error response
     */
    @PutMapping("/{userId}")
    @ApiOperation(value = "Update an existing user", notes = "Updates a user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "User successfully updated"),
        @ApiResponse(code = 400, message = "Invalid input data"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable 
            @ApiParam(value = "User ID", required = true) 
            Long userId,
            @Valid @RequestBody 
            @ApiParam(value = "Updated user details", required = true) 
            UserRequest request) {
        log.debug("REST request to update User with id {}: {}", userId, request);
        
        try {
            // TODO: Check if user exists before attempting update
            UserResponse response = userService.updateUser(userId, request);
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating user with ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @return ResponseEntity containing the deletion confirmation or an error response
     */
    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Delete a user", notes = "Deletes a user based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "User successfully deleted"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<UserResponse> deleteUser(
            @PathVariable 
            @ApiParam(value = "User ID", required = true) 
            Long userId) {
        log.debug("REST request to delete User with id: {}", userId);
        
        try {
            UserResponse response = userService.deleteUser(userId);
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting user with ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}