package com.gradle80.test.utils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * Utility class providing helper methods for testing purposes.
 * Contains methods for random data generation and result comparison.
 */
public class TestUtils {
    
    // SecureRandom instance for generating random data
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // Character set used for random string generation
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private TestUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
    
    /**
     * Generates a random string of specified length
     * 
     * @param length The length of the random string to generate
     * @return A randomly generated string
     * @throws IllegalArgumentException if length is negative
     */
    public static String createRandomData(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative");
        }
        
        if (length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(randomIndex));
        }
        
        return sb.toString();
    }
    
    /**
     * Compares two objects for equality, handling null values
     * 
     * @param expected The expected object
     * @param actual The actual object to compare against the expected value
     * @return true if objects are equal, false otherwise
     */
    public static boolean compareResults(Object expected, Object actual) {
        // Handle case where both objects are null
        if (expected == null && actual == null) {
            return true;
        }
        
        // Handle case where only one object is null
        if (expected == null || actual == null) {
            return false;
        }
        
        // If the objects are of the same class, use equals method
        if (expected.getClass().equals(actual.getClass())) {
            return Objects.equals(expected, actual);
        }
        
        // TODO: Add special handling for comparing different types that might be equivalent
        // For example, comparing a Long to an Integer
        
        // FIXME: Implement deep comparison for arrays and collections
        
        // Default to string comparison if types don't match
        return expected.toString().equals(actual.toString());
    }
}