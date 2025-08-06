package com.gradle80.web.filter;

import com.gradle80.web.context.RequestContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Filter responsible for initializing and managing the request context.
 * This filter sets up a ThreadLocal context at the beginning of each request
 * and cleans it up at the end to prevent memory leaks.
 * 
 * It should be configured to run early in the filter chain to ensure the
 * context is available to all subsequent filters and request handlers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/*")
public class RequestContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Initializing RequestContextFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        try {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                // Initialize context for this request
                RequestContext context = initializeContext(httpRequest);
                
                // Add the request ID to the response headers for tracking
                httpResponse.setHeader(REQUEST_ID_HEADER, context.getRequestId());
                
                logger.debug("Request context initialized with ID: {}", context.getRequestId());
            }
            
            // Continue with the filter chain
            chain.doFilter(request, response);
        } finally {
            // Always clean up the context to prevent memory leaks
            RequestContext.clear();
            logger.trace("Request context cleared");
        }
    }

    @Override
    public void destroy() {
        logger.info("Destroying RequestContextFilter");
    }
    
    /**
     * Initializes the request context with information from the HTTP request.
     * This method extracts relevant information from the request and security context,
     * creates a new RequestContext, and sets it as the current context.
     *
     * @param request The HTTP request
     * @return The initialized RequestContext
     */
    RequestContext initializeContext(HttpServletRequest request) {
        // Get or generate a request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.trim().isEmpty()) {
            // Generate a new UUID if no request ID was provided
            requestId = UUID.randomUUID().toString();
        }
        
        // Try to get user ID from security context if user is authenticated
        Long userId = extractUserIdFromSecurityContext();
        
        // Create and initialize the context
        RequestContext context = new RequestContext(
            requestId,
            userId,
            System.currentTimeMillis()
        );
        
        // Set as current context
        RequestContext.setCurrentContext(context);
        
        return context;
    }
    
    /**
     * Attempts to extract the user ID from the current security context.
     * Returns null if no authentication is present or the user ID can't be determined.
     *
     * @return The user ID or null if not available
     */
    private Long extractUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // FIXME: This implementation assumes the principal has a 'getId' method or is convertible to Long.
        // It should be adapted based on the actual authentication implementation.
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Object principal = authentication.getPrincipal();
                // This is a placeholder for actual user ID extraction logic
                // TODO: Implement proper user ID extraction based on authentication scheme
                
                // Example (to be replaced with actual implementation):
                if (principal instanceof Long) {
                    return (Long) principal;
                } 
                // Add additional checks based on your security model
                
                logger.debug("Unable to extract user ID from principal: {}", principal);
            } catch (Exception e) {
                logger.warn("Error extracting user ID from security context", e);
            }
        }
        
        return null;
    }
}