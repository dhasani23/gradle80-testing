package com.gradle80.common.security;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for SecurityUtils.
 */
public class SecurityUtilsTest {

    @Test
    public void testGenerateSecureToken() {
        String token1 = SecurityUtils.generateSecureToken();
        String token2 = SecurityUtils.generateSecureToken();
        
        assertNotNull("Token should not be null", token1);
        assertNotNull("Token should not be null", token2);
        assertNotEquals("Tokens should be unique", token1, token2);
        assertTrue("Token should have sufficient length", token1.length() >= 32);
    }
    
    @Test
    public void testHashPassword() {
        String password = "Test@Password123";
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        assertNotNull("Hashed password should not be null", hashedPassword);
        assertTrue("Hashed password should contain salt and hash parts", hashedPassword.contains(":"));
        
        String[] parts = hashedPassword.split(":");
        assertEquals("Hashed password should have exactly two parts", 2, parts.length);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHashPasswordWithNull() {
        SecurityUtils.hashPassword(null);
    }
    
    @Test
    public void testValidatePassword() {
        String password = "Test@Password123";
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        assertTrue("Password validation should succeed with correct password", 
                SecurityUtils.validatePassword(password, hashedPassword));
                
        assertFalse("Password validation should fail with incorrect password", 
                SecurityUtils.validatePassword("WrongPassword", hashedPassword));
    }
    
    @Test
    public void testValidatePasswordWithNullInputs() {
        assertFalse("Password validation should fail with null raw password", 
                SecurityUtils.validatePassword(null, "salt:hash"));
                
        assertFalse("Password validation should fail with null encoded password", 
                SecurityUtils.validatePassword("password", null));
                
        assertFalse("Password validation should fail with both null inputs", 
                SecurityUtils.validatePassword(null, null));
    }
    
    @Test
    public void testValidatePasswordWithInvalidFormat() {
        assertFalse("Password validation should fail with invalid format", 
                SecurityUtils.validatePassword("password", "invalid-format"));
    }
}