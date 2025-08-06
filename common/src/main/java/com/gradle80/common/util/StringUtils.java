package com.gradle80.common.util;

/**
 * Utility class providing various string manipulation methods.
 * 
 * This class contains static utility methods for common string operations
 * like checking for null/empty strings, creating URL-friendly slugs,
 * and truncating strings to a maximum length.
 */
public final class StringUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param str the string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Converts a string to a URL-friendly slug.
     * 
     * The method performs the following operations:
     * - Converts to lowercase
     * - Removes special characters
     * - Replaces spaces with hyphens
     * - Eliminates consecutive hyphens
     *
     * @param input the string to convert to a slug
     * @return a URL-friendly slug, or empty string if input is null
     */
    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        
        // Convert to lowercase
        String slug = input.toLowerCase();
        
        // Replace spaces with hyphens
        slug = slug.replaceAll("\\s+", "-");
        
        // Remove special characters
        slug = slug.replaceAll("[^a-z0-9-]", "");
        
        // Remove consecutive hyphens
        slug = slug.replaceAll("-+", "-");
        
        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-|-$", "");
        
        return slug;
    }

    /**
     * Truncates a string to the specified maximum length.
     * 
     * If the input string is longer than maxLength, it will be truncated and
     * appended with an ellipsis ("..."). The ellipsis is included in the maxLength.
     *
     * @param input the string to truncate
     * @param maxLength the maximum allowed length (including ellipsis if added)
     * @return the truncated string, or null if input is null
     * @throws IllegalArgumentException if maxLength is negative
     */
    public static String truncate(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength cannot be negative");
        }
        
        // Return as is if input length is less than or equal to maxLength
        if (input.length() <= maxLength) {
            return input;
        }
        
        // FIXME: Consider Unicode surrogate pairs when truncating
        // Truncating at byte level might split surrogate pairs, causing display issues
        
        // Truncate and add ellipsis if there's room
        if (maxLength > 3) {
            return input.substring(0, maxLength - 3) + "...";
        } else {
            // If maxLength is too small for ellipsis, just truncate
            return input.substring(0, maxLength);
        }
    }
    
    // TODO: Add support for more string manipulation methods like:
    // - capitalize(String) - Capitalizes first letter of each word
    // - reverse(String) - Reverses a string
    // - countOccurrences(String, String) - Counts occurrences of substring
}