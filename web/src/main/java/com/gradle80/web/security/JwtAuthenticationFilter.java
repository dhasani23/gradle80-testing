package com.gradle80.web.security;

import com.gradle80.api.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT authentication filter that intercepts HTTP requests to validate JWT tokens.
 * This filter extracts the JWT token from the Authorization header, validates it,
 * and sets up the Spring Security context if the token is valid.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final AuthenticationService authenticationService;
    
    /**
     * Constructor for the JWT authentication filter.
     *
     * @param authenticationService The service used for token validation
     */
    @Autowired
    public JwtAuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Main filter method that processes each HTTP request.
     * Extracts the JWT token, validates it, and sets up the security context.
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain
     * @throws ServletException If a servlet exception occurs
     * @throws IOException      If an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractToken(request);
            
            if (StringUtils.hasText(jwt) && validateToken(jwt)) {
                // TODO: Extract username and roles from token to create proper authorities
                // Currently using a placeholder implementation
                Authentication authentication = createAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Set authentication in security context for '{}'", authentication.getName());
            } else {
                logger.debug("No valid JWT token found in request headers");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // FIXME: Consider more specific exception handling based on error type
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request The HTTP request
     * @return The JWT token or null if not found
     */
    protected String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    /**
     * Validates the JWT token using the authentication service.
     *
     * @param token The JWT token to validate
     * @return True if the token is valid, false otherwise
     */
    private boolean validateToken(String token) {
        return authenticationService.validateToken(token);
    }
    
    /**
     * Creates an Authentication object for the security context.
     *
     * @param token The validated JWT token
     * @return An Authentication object
     */
    private Authentication createAuthentication(String token) {
        // TODO: Implement proper user details extraction from token
        // This is a placeholder implementation
        UserDetails userDetails = new User(
            "user", // Replace with actual username from token
            "", // No password needed as authentication is done via token
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        return new UsernamePasswordAuthenticationToken(
            userDetails, 
            null, 
            userDetails.getAuthorities()
        );
    }
}