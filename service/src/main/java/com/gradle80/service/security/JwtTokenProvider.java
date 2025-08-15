package com.gradle80.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Provider for JWT token generation and validation.
 * Handles token creation, username extraction, and token validation.
 */
@Component
public class JwtTokenProvider {

    /**
     * Secret key used for JWT token signing and validation.
     * Should be injected from application properties.
     */
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    /**
     * Token validity duration in milliseconds.
     * Default is 3600000 ms (1 hour).
     */
    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    /**
     * Encodes the secret key with Base64 after initialization.
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Creates a JWT token for the specified user with the given roles.
     *
     * @param username the username to include in the token
     * @param roles the list of roles assigned to the user
     * @return generated JWT token as a string
     */
    public String createToken(String username, List<String> roles) {
        // Set token claims (payload data)
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        // Set creation time and expiration time
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // Generate and return the token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates a JWT token.
     * Checks if the token is well-formed, has a valid signature, and is not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Parse token and validate signature
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            
            // If parsing completes without exceptions, token is valid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid or expired
            // FIXME: Consider using a logger instead of System.err in production code
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extracts the roles from a JWT token.
     *
     * @param token the JWT token
     * @return list of roles contained in the token
     * @throws JwtException if token parsing fails
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        // TODO: Add error handling for case when 'roles' claim is missing
        return (List<String>) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }
    
    /**
     * Checks if a token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
                    
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; // If we can't parse the token, consider it expired
        }
    }
    
    /**
     * Gets the token validity duration in milliseconds.
     *
     * @return token validity duration in milliseconds
     */
    public long getValidityInMilliseconds() {
        return validityInMilliseconds;
    }
}