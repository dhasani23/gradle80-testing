package com.gradle80.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.api.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Authentication entry point for handling unauthorized access attempts.
 * This component is triggered when an unauthenticated user attempts to access
 * secured resources that require authentication.
 * 
 * It returns a properly formatted JSON error response instead of the default
 * redirect to a login page or a basic auth challenge.
 */
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON object mapper for serializing error responses to JSON format
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructs an AuthEntryPoint with the necessary dependencies.
     *
     * @param objectMapper the object mapper for JSON serialization
     */
    public AuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * This method is called whenever an AuthenticationException is thrown in the application 
     * due to a user trying to access a protected resource without proper authentication.
     * 
     * It sets the appropriate HTTP status code (401 Unauthorized) and writes a JSON response
     * with error details to the HTTP response.
     *
     * @param request the request that resulted in an AuthenticationException
     * @param response the response to modify to inform the client about the authentication failure
     * @param ex the exception that was thrown when attempting to authenticate
     * @throws IOException if an I/O error occurs during the writing of the error response
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
            AuthenticationException ex) throws IOException, ServletException {
        
        // Set proper content type for JSON response
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Set HTTP status code to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Create an error response object
        ErrorResponse errorResponse = new ErrorResponse(
                "Unauthorized: Authentication is required to access this resource",
                "UNAUTHORIZED",
                Collections.singletonList(ex.getMessage())
        );
        
        // Write the error response as JSON to the response output stream
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
        
        // FIXME: Consider adding more detailed logging for security audit purposes
        
        // TODO: Consider tracking failed authentication attempts for potential security monitoring
    }
}