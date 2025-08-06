package com.gradle80.service.implementation;

import com.gradle80.api.request.AuthenticationRequest;
import com.gradle80.api.response.AuthenticationResponse;
import com.gradle80.api.service.AuthenticationService;
import com.gradle80.data.entity.UserEntity;
import com.gradle80.data.repository.UserRepository;
import com.gradle80.service.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the authentication service.
 * Provides functionality for user authentication, token validation, and token refresh.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Constructor with dependency injection for required components.
     *
     * @param userRepository  repository for user data access
     * @param passwordEncoder encoder for password validation
     * @param tokenProvider   provider for JWT token operations
     */
    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Authenticates a user based on provided credentials.
     * If authentication is successful, generates and returns a JWT token.
     *
     * @param request the authentication request containing credentials
     * @return authentication response with token on success, or error message on failure
     * @throws IllegalArgumentException if the request is null or invalid
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Authentication request cannot be null");
        }
        
        if (!request.validate()) {
            return AuthenticationResponse.failure("Invalid authentication request");
        }

        try {
            // Find the user by username
            UserEntity user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                LOGGER.log(Level.INFO, "Authentication failed: user not found for username: {0}", request.getUsername());
                return AuthenticationResponse.failure("Invalid username or password");
            }
            
            // Check if user is active
            if (!user.isActive()) {
                LOGGER.log(Level.INFO, "Authentication failed: account is deactivated for username: {0}", request.getUsername());
                return AuthenticationResponse.failure("Account is deactivated");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                LOGGER.log(Level.INFO, "Authentication failed: invalid password for username: {0}", request.getUsername());
                return AuthenticationResponse.failure("Invalid username or password");
            }

            // Generate token
            // TODO: Implement role-based authorization and retrieve roles from user
            List<String> roles = Arrays.asList("ROLE_USER"); // Default role for now
            String token = tokenProvider.createToken(user.getUsername(), roles);
            
            // Calculate token expiration time
            long expiresAt = System.currentTimeMillis() + tokenProvider.getValidityInMilliseconds();
            
            // TODO: Update last login timestamp for the user
            
            LOGGER.log(Level.INFO, "User authenticated successfully: {0}", user.getUsername());
            return AuthenticationResponse.success(token, expiresAt);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
            return AuthenticationResponse.failure("Authentication failed due to internal error");
        }
    }

    /**
     * Validates if a token is valid and not expired.
     *
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     * @throws IllegalArgumentException if token is null or empty
     */
    @Override
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        try {
            return tokenProvider.validateToken(token);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Token validation failed", e);
            return false;
        }
    }

    /**
     * Refreshes an existing token if it is still valid.
     * Issues a new token with extended expiration time.
     *
     * @param token the JWT token to refresh
     * @return authentication response with new token
     * @throws IllegalArgumentException if token is null, empty, or invalid
     * @throws SecurityException if token has already expired
     */
    @Override
    public AuthenticationResponse refreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        try {
            // Validate the token first
            if (!tokenProvider.validateToken(token)) {
                LOGGER.log(Level.INFO, "Token refresh failed: invalid token");
                throw new SecurityException("Invalid token");
            }
            
            // Check if token is expired (additional check)
            if (tokenProvider.isTokenExpired(token)) {
                LOGGER.log(Level.INFO, "Token refresh failed: token expired");
                throw new SecurityException("Token has expired and cannot be refreshed");
            }
            
            // Extract user details from token
            String username = tokenProvider.getUsername(token);
            List<String> roles = tokenProvider.getRoles(token);
            
            // Verify user still exists and is active
            UserEntity user = userRepository.findByUsername(username);
            if (user == null) {
                LOGGER.log(Level.WARNING, "Token refresh failed: user no longer exists: {0}", username);
                throw new UsernameNotFoundException("User not found");
            }
            
            if (!user.isActive()) {
                LOGGER.log(Level.WARNING, "Token refresh failed: user account deactivated: {0}", username);
                throw new SecurityException("User account is deactivated");
            }
            
            // Generate new token
            String newToken = tokenProvider.createToken(username, roles);
            long expiresAt = System.currentTimeMillis() + tokenProvider.getValidityInMilliseconds();
            
            LOGGER.log(Level.INFO, "Token refreshed successfully for user: {0}", username);
            return AuthenticationResponse.success(newToken, expiresAt);
            
        } catch (SecurityException e) {
            LOGGER.log(Level.INFO, "Token refresh failed: {0}", e.getMessage());
            return AuthenticationResponse.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during token refresh", e);
            return AuthenticationResponse.failure("Token refresh failed due to internal error");
        }
    }
    
    /**
     * Updates the user's last login date after successful authentication.
     * 
     * @param username the username of the authenticated user
     * @throws UsernameNotFoundException if the user doesn't exist
     */
    private void updateLastLogin(String username) {
        // FIXME: Implement this method once the UserRepositoryCustom interface includes updateLastLoginDate
        // For now, we'll just log that this would be updated
        LOGGER.log(Level.INFO, "Would update last login date for user: {0}", username);
        
        // TODO: Implement with actual update once the repository method is available
        // UserEntity user = userRepository.findByUsername(username);
        // userRepository.updateLastLoginDate(user.getId(), new Date());
    }
}