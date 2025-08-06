package com.gradle80.common.validation;

import com.gradle80.common.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for common validation tasks.
 * 
 * This class contains static methods to validate different types of inputs
 * such as non-null values, non-empty strings, and email addresses.
 */
public final class ValidationUtils {

    /**
     * Regular expression pattern for validating email addresses.
     * This pattern checks for the basic format of an email: 
     * - Contains a local part (username)
     * - An @ symbol
     * - A domain part with at least one dot
     */
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Validates that the provided object is not null.
     * 
     * @param object the object to validate
     * @param fieldName the name of the field being validated (for error message)
     * @throws ValidationException if the object is null
     */
    public static void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new ValidationException(fieldName + " must not be null");
        }
    }
    
    /**
     * Validates that the provided string is not null or empty.
     * 
     * @param text the string to validate
     * @param fieldName the name of the field being validated (for error message)
     * @throws ValidationException if the string is null or empty
     */
    public static void validateNotEmpty(String text, String fieldName) {
        validateNotNull(text, fieldName);
        
        if (text.trim().isEmpty()) {
            throw new ValidationException(fieldName + " must not be empty");
        }
    }
    
    /**
     * Validates that the provided string is a valid email address.
     * 
     * @param email the email address to validate
     * @throws ValidationException if the email is invalid
     */
    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
        
        // FIXME: This simple regex might not catch all invalid email cases
        // TODO: Consider using a more comprehensive email validation library
    }
    
    /**
     * Validates that the provided integer is positive.
     * 
     * @param value the integer to validate
     * @param fieldName the name of the field being validated (for error message)
     * @throws ValidationException if the value is not positive
     */
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
    
    /**
     * Validates that the provided integer is within the specified range.
     * 
     * @param value the integer to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @param fieldName the name of the field being validated (for error message)
     * @throws ValidationException if the value is outside the specified range
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName + " must be between " + min + " and " + max + " (inclusive)");
        }
    }
}