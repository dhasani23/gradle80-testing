package com.gradle80.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gradle80.api.request.AuthenticationRequest;
import com.gradle80.api.response.AuthenticationResponse;
import com.gradle80.api.service.AuthenticationService;
import com.gradle80.web.filter.ResponseEnhancer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for authentication operations.
 * This controller handles user authentication, token validation, and token refresh.
 *
 * @author gradle80
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@Api(tags = "Authentication", description = "Authentication operations")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final ResponseEnhancer responseEnhancer;

    /**
     * Constructor for AuthController
     * 
     * @param authenticationService Service for authentication operations
     * @param responseEnhancer Response enhancement utility
     */
    @Autowired
    public AuthController(AuthenticationService authenticationService, ResponseEnhancer responseEnhancer) {
        this.authenticationService = authenticationService;
        this.responseEnhancer = responseEnhancer;
    }

    /**
     * Authenticate a user and return a JWT token
     * 
     * @param request The authentication request containing username and password
     * @return ResponseEntity with authentication token information
     */
    @PostMapping("/login")
    @ApiOperation(value = "Authenticate user", notes = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully authenticated"),
        @ApiResponse(code = 401, message = "Invalid credentials"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<AuthenticationResponse> authenticate(
            @ApiParam(value = "Authentication request", required = true) 
            @RequestBody AuthenticationRequest request) {
        
        log.debug("REST request to authenticate user: {}", request.getUsername());
        
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            // The exception will be handled by the global exception handler
            throw e;
        }
    }

    /**
     * Validate a JWT token
     * 
     * @param token The JWT token to validate
     * @return ResponseEntity with boolean indicating if token is valid
     */
    @GetMapping("/validate")
    @ApiOperation(value = "Validate token", notes = "Validates a JWT token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Token validation result"),
        @ApiResponse(code = 400, message = "Invalid token format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Boolean> validateToken(
            @ApiParam(value = "JWT token", required = true)
            @RequestParam String token) {
        
        log.debug("REST request to validate token");
        
        try {
            boolean isValid = authenticationService.validateToken(token);
            return responseEnhancer.enhance(isValid, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            // The exception will be handled by the global exception handler
            throw e;
        }
    }

    /**
     * Refresh an expired JWT token
     * 
     * @param token The expired JWT token
     * @return ResponseEntity with new token information
     */
    @PostMapping("/refresh")
    @ApiOperation(value = "Refresh token", notes = "Refreshes an expired JWT token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Token refreshed successfully"),
        @ApiResponse(code = 400, message = "Invalid token format"),
        @ApiResponse(code = 401, message = "Invalid refresh token"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @ApiParam(value = "JWT refresh token", required = true)
            @RequestParam String token) {
        
        log.debug("REST request to refresh token");
        
        try {
            // FIXME: Consider adding token blacklisting for revoked/expired tokens
            AuthenticationResponse response = authenticationService.refreshToken(token);
            return responseEnhancer.enhance(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            // The exception will be handled by the global exception handler
            throw e;
        }
    }
    
    // TODO: Add logout endpoint to invalidate tokens
    
    // TODO: Add password reset functionality
}