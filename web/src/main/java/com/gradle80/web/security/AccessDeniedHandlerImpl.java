package com.gradle80.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.api.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom implementation of AccessDeniedHandler to provide a standardized JSON
 * response when a user is denied access to a protected resource due to
 * insufficient permissions.
 * 
 * This handler is called when an authenticated user attempts to access
 * resources they do not have permission to access.
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);
    
    /**
     * JSON mapper for serializing error responses
     */
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor for dependency injection
     *
     * @param objectMapper JSON object mapper
     */
    @Autowired
    public AccessDeniedHandlerImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Handles an access denied failure by generating a standardized JSON error response
     * with HTTP status 403 (Forbidden).
     *
     * @param request The HTTP request that resulted in an AccessDeniedException
     * @param response The HTTP response
     * @param ex The access denied exception
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException ex) throws IOException, ServletException {
        
        logger.warn("Access denied for request: {} {}, User: {}", 
                request.getMethod(), 
                request.getRequestURI(),
                request.getRemoteUser());
        
        // Create detailed error response
        List<String> details = new ArrayList<>();
        details.add("You don't have permission to access this resource");
        details.add("Required role or authority is missing");
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Access Denied", 
                "FORBIDDEN", 
                details);
        
        // Set HTTP response properties
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // Write error response as JSON
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
        
        // FIXME: Consider adding request path to error details for better debugging
        
        // TODO: Implement audit logging for security violations
    }
}