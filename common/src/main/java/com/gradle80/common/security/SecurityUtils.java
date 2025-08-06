package com.gradle80.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class providing security-related operations such as token generation,
 * password hashing, and password validation.
 *
 * @author Gradle80
 */
public final class SecurityUtils {
    
    private static final int TOKEN_LENGTH = 32;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SecurityUtils() {
        throw new AssertionError("SecurityUtils class should not be instantiated");
    }
    
    /**
     * Generates a secure random token that can be used for authentication or verification purposes.
     * 
     * @return a Base64 encoded secure random token
     */
    public static String generateSecureToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Hashes a password using SHA-256 algorithm with a random salt.
     * The returned string format is: Base64(salt):Base64(hash)
     * 
     * @param password the password to hash, cannot be null
     * @return the hashed password with salt
     * @throws IllegalArgumentException if password is null
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        try {
            // Generate a random salt
            byte[] salt = new byte[16];
            SECURE_RANDOM.nextBytes(salt);
            
            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Encode salt and hash to Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Format: salt:hash
            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            // This should not happen as SHA-256 is a standard algorithm
            throw new RuntimeException("Failed to hash password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates a raw password against a previously hashed password.
     * 
     * @param rawPassword the raw password to check
     * @param encodedPassword the previously hashed password (in format: Base64(salt):Base64(hash))
     * @return true if the password matches, false otherwise
     */
    public static boolean validatePassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        // Split the stored password into salt and hash parts
        String[] parts = encodedPassword.split(":");
        if (parts.length != 2) {
            // Invalid format
            return false;
        }
        
        try {
            // Extract salt and expected hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] actualHash = md.digest(rawPassword.getBytes());
            
            // Compare the hashes
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException | NoSuchAlgorithmException e) {
            // Invalid Base64 or algorithm not found
            // Log this error in a production environment
            return false;
        }
    }
    
    /**
     * TODO: Add support for stronger hashing algorithms like BCrypt or Argon2
     * which are more resistant to brute force attacks.
     */
    
    /**
     * FIXME: The current implementation does not include iteration count for 
     * password hashing, which would improve security against brute force attacks.
     */
}