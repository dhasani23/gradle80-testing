package com.gradle80.api.service;

import com.gradle80.api.request.AuthenticationRequest;
import com.gradle80.api.response.AuthenticationResponse;

/**
 * Authentication service interface that provides methods for user authentication
 * and token management. This service handles user login, token validation, and
 * token refresh operations.
 * 
 * @since 1.0.0
 */
public interface AuthenticationService {
    
    /**
     * Authenticates a user based on credentials provided in the authentication request.
     * On successful authentication, returns a response containing an authentication token
     * and its expiration time.
     *
     * @param request the authentication request containing user credentials
     * @return an authentication response with token on success, or error details on failure
     * @throws IllegalArgumentException if the request is null or invalid
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
    
    /**
     * Validates whether an authentication token is valid and not expired.
     *
     * @param token the authentication token to validate
     * @return true if the token is valid and not expired, false otherwise
     * @throws IllegalArgumentException if the token is null or empty
     */
    boolean validateToken(String token);
    
    /**
     * Refreshes an existing authentication token, extending its validity period.
     * This operation will only succeed if the original token is still valid.
     *
     * @param token the authentication token to refresh
     * @return a new authentication response with a fresh token and updated expiration time
     * @throws IllegalArgumentException if the token is null, empty, or invalid
     * @throws SecurityException if the token has already expired
     * 
     * TODO: Consider implementing sliding window expiration for better UX
     */
    AuthenticationResponse refreshToken(String token);
    
    // FIXME: Add method for token invalidation/logout to properly handle user sessions
}