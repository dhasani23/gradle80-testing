package com.gradle80.web.util;

import com.gradle80.api.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for HTTP response generation.
 * Provides standardized methods for creating common response types.
 */
public final class ResponseUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ResponseUtil() {
        // Private constructor to hide implicit public one
    }

    /**
     * Wraps an optional value into a ResponseEntity.
     * If the optional is present, returns a 200 OK with the value.
     * If the optional is empty, returns a 404 Not Found.
     *
     * @param maybeResponse the optional value to wrap
     * @param <T> the type of the response
     * @return a ResponseEntity containing the value or 404 if empty
     */
    public static <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(response -> ResponseEntity.ok().body(response))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a success response with the provided body and 200 OK status.
     *
     * @param body the response body
     * @param <T> the type of the response body
     * @return a ResponseEntity with 200 OK status and the provided body
     */
    public static <T> ResponseEntity<T> createSuccessResponse(T body) {
        return ResponseEntity.ok(body);
    }

    /**
     * Creates an error response with the provided error code, message, and HTTP status.
     *
     * @param errorCode the error code to include in the response
     * @param message the error message
     * @param status the HTTP status for the response
     * @return a ResponseEntity with the specified status and an ErrorResponse body
     */
    public static ResponseEntity<ErrorResponse> createErrorResponse(String errorCode, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(message, errorCode);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Creates an error response with details.
     *
     * @param errorCode the error code to include in the response
     * @param message the error message
     * @param details list of detailed error messages
     * @param status the HTTP status for the response
     * @return a ResponseEntity with the specified status and an ErrorResponse body
     */
    public static ResponseEntity<ErrorResponse> createErrorResponse(
            String errorCode, String message, List<String> details, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(message, errorCode, details);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Creates an error response for validation errors.
     *
     * @param message the error message
     * @param details list of validation error details
     * @return a ResponseEntity with 400 Bad Request status and an ErrorResponse body
     */
    public static ResponseEntity<ErrorResponse> createValidationErrorResponse(String message, List<String> details) {
        return createErrorResponse("VALIDATION_ERROR", message, details, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a not found error response.
     *
     * @param resourceType the type of resource that wasn't found
     * @param id the identifier that was used to look up the resource
     * @return a ResponseEntity with 404 Not Found status and an ErrorResponse body
     */
    public static ResponseEntity<ErrorResponse> createNotFoundResponse(String resourceType, Object id) {
        List<String> details = new ArrayList<>();
        details.add(resourceType + " with id " + id + " not found");
        return createErrorResponse(
                "NOT_FOUND", 
                resourceType + " not found", 
                details, 
                HttpStatus.NOT_FOUND);
    }

    // TODO: Add methods for other common response types as needed

    /**
     * FIXME: Consider integrating with a standard error code enum to ensure consistency
     * across the application.
     */
}