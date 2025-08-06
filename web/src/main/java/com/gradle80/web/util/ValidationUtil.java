package com.gradle80.web.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class for validating request objects.
 * 
 * This class provides methods to validate fields in request objects,
 * checking for null values, empty strings, and basic pattern matching.
 * 
 * @author gradle80
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");
    
    /**
     * Validates an object's fields and returns a list of validation error messages.
     * 
     * This method uses reflection to inspect the fields of the provided object
     * and performs various validation checks based on field type:
     * - Checks for null values on all fields
     * - For String fields, checks for empty strings
     * - For fields with recognized names (email, phone), performs pattern matching
     *
     * @param object The object to validate
     * @return A list of validation error messages. Empty list if validation passes.
     */
    public static List<String> validateRequestFields(Object object) {
        List<String> validationErrors = new ArrayList<>();
        
        if (object == null) {
            validationErrors.add("Request object cannot be null");
            return validationErrors;
        }
        
        try {
            // Get all declared fields from the object's class
            Field[] fields = object.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(object);
                
                // Skip static and final fields
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || 
                    java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                // Basic null check for all fields
                if (value == null) {
                    validationErrors.add(fieldName + " cannot be null");
                    continue;
                }
                
                // Type-specific validations
                if (String.class.equals(field.getType())) {
                    String stringValue = (String) value;
                    if (stringValue.trim().isEmpty()) {
                        validationErrors.add(fieldName + " cannot be empty");
                    }
                    
                    // Field name-specific validations
                    if (fieldName.toLowerCase().contains("email")) {
                        if (!EMAIL_PATTERN.matcher(stringValue).matches()) {
                            validationErrors.add(fieldName + " is not a valid email address");
                        }
                    } else if (fieldName.toLowerCase().contains("phone")) {
                        if (!PHONE_PATTERN.matcher(stringValue).matches()) {
                            validationErrors.add(fieldName + " is not a valid phone number");
                        }
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    List<?> listValue = (List<?>) value;
                    if (listValue.isEmpty()) {
                        // FIXME: This might be a valid case for some requests, consider making this configurable
                        validationErrors.add(fieldName + " list cannot be empty");
                    } else {
                        // TODO: Add nested validation for list elements if needed
                    }
                }
                
                // TODO: Add additional validations for numeric fields (min/max values)
                // TODO: Add validation for date fields (valid ranges)
            }
        } catch (IllegalAccessException e) {
            validationErrors.add("Error during validation: " + e.getMessage());
        }
        
        return validationErrors;
    }
    
    /**
     * Validates if the given string is a valid email address.
     * 
     * @param email The email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates if the given string is a valid phone number.
     * 
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Checks if a string is null or empty.
     * 
     * @param value The string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Checks if a list is null or empty.
     * 
     * @param list The list to check
     * @return true if the list is null or empty, false otherwise
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}