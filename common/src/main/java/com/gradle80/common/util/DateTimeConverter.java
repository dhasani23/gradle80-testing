package com.gradle80.common.util;

import java.time.LocalDateTime;

/**
 * Interface for date/time conversions.
 * 
 * Provides methods to convert between LocalDateTime objects and String representations.
 * Implementations should define the specific format used for the string representation.
 */
public interface DateTimeConverter {
    
    /**
     * Converts a LocalDateTime object to its String representation.
     *
     * @param dateTime The LocalDateTime to convert
     * @return The string representation of the provided date/time
     * @throws NullPointerException if dateTime is null
     */
    String convertToString(LocalDateTime dateTime);
    
    /**
     * Parses a string representation into a LocalDateTime object.
     *
     * @param dateTimeString The string to parse
     * @return The LocalDateTime object represented by the string
     * @throws IllegalArgumentException if the string cannot be parsed
     * @throws NullPointerException if dateTimeString is null
     */
    LocalDateTime convertFromString(String dateTimeString);
}