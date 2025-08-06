package com.gradle80.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Utility class for JSON serialization and deserialization operations.
 * Provides methods to convert objects to JSON strings and vice versa.
 */
public class JsonUtils {

    /**
     * The ObjectMapper instance used for JSON operations.
     * This is thread-safe and reused across all methods.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private JsonUtils() {
        // Utility class should not be instantiated
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object The object to serialize
     * @return A JSON string representation of the object
     * @throws IllegalArgumentException if serialization fails
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json  The JSON string to deserialize
     * @param clazz The class to convert the JSON to
     * @param <T>   The type of the resulting object
     * @return An instance of type T created from the JSON string
     * @throws IllegalArgumentException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to " + clazz.getSimpleName(), e);
        }
    }

    // TODO: Add support for handling generic types using TypeReference

    // TODO: Consider adding methods for reading from and writing to files

    /**
     * Checks if a string is valid JSON.
     *
     * @param json The string to check
     * @return true if the string is valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}