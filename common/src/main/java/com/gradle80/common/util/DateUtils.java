package com.gradle80.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date and time operations.
 * 
 * This class provides methods for formatting and parsing dates and times
 * using standard formats. It uses Java 8 time API for all date/time operations.
 */
public final class DateUtils {

    /**
     * The default format pattern for dates (yyyy-MM-dd).
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * The default format pattern for times (HH:mm:ss).
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    
    /**
     * The default format pattern for date-time (yyyy-MM-dd HH:mm:ss).
     */
    private static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;
    
    /**
     * DateTimeFormatter for dates based on the DEFAULT_DATE_FORMAT.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    
    /**
     * DateTimeFormatter for date-time based on the DEFAULT_DATETIME_FORMAT.
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Formats a LocalDate object to a string using the default date format.
     *
     * @param date The date to format
     * @return A formatted date string
     * @throws NullPointerException if date is null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            throw new NullPointerException("Date cannot be null");
        }
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Formats a LocalDateTime object to a string using the default date-time format.
     *
     * @param dateTime The date-time to format
     * @return A formatted date-time string
     * @throws NullPointerException if dateTime is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new NullPointerException("DateTime cannot be null");
        }
        return DATETIME_FORMATTER.format(dateTime);
    }
    
    /**
     * Parses a date string to a LocalDate object using the default date format.
     *
     * @param dateStr The date string to parse
     * @return The parsed LocalDate
     * @throws NullPointerException if dateStr is null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null) {
            throw new NullPointerException("Date string cannot be null");
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    /**
     * Parses a date-time string to a LocalDateTime object using the default date-time format.
     *
     * @param dateTimeStr The date-time string to parse
     * @return The parsed LocalDateTime
     * @throws NullPointerException if dateTimeStr is null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            throw new NullPointerException("DateTime string cannot be null");
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    /**
     * Formats a LocalDate object with a custom format.
     *
     * @param date The date to format
     * @param pattern The format pattern to use
     * @return A formatted date string
     * @throws NullPointerException if date or pattern is null
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            throw new NullPointerException("Date and pattern cannot be null");
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }
    
    /**
     * Formats a LocalDateTime object with a custom format.
     *
     * @param dateTime The date-time to format
     * @param pattern The format pattern to use
     * @return A formatted date-time string
     * @throws NullPointerException if dateTime or pattern is null
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            throw new NullPointerException("DateTime and pattern cannot be null");
        }
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }
    
    /**
     * Parses a date string with a custom format.
     *
     * @param dateStr The date string to parse
     * @param pattern The format pattern to use
     * @return The parsed LocalDate
     * @throws NullPointerException if dateStr or pattern is null
     * @throws DateTimeParseException if the text cannot be parsed
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || pattern == null) {
            throw new NullPointerException("Date string and pattern cannot be null");
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Parses a date-time string with a custom format.
     *
     * @param dateTimeStr The date-time string to parse
     * @param pattern The format pattern to use
     * @return The parsed LocalDateTime
     * @throws NullPointerException if dateTimeStr or pattern is null
     * @throws DateTimeParseException if the text cannot be parsed
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || pattern == null) {
            throw new NullPointerException("DateTime string and pattern cannot be null");
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Returns the current date.
     *
     * @return The current date as LocalDate
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Returns the current date-time.
     *
     * @return The current date-time as LocalDateTime
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    // TODO: Add methods to support time zone conversions
    
    // FIXME: Improve error handling for parse methods to provide more meaningful error messages
}