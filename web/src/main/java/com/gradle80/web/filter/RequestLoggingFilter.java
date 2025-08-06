package com.gradle80.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Filter for logging incoming HTTP requests.
 * This filter captures and logs details about incoming requests
 * including URI, method, headers, and request parameters.
 * 
 * @author gradle80
 */
@Component
public class RequestLoggingFilter implements Filter {
    
    /**
     * Logger instance for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Initializing Request Logging Filter");
    }
    
    /**
     * Main filter method to process and log incoming HTTP requests.
     * 
     * @param request the incoming servlet request
     * @param response the outgoing servlet response
     * @param chain the filter chain for request processing
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // Generate a unique request ID for traceability
            String requestId = UUID.randomUUID().toString();
            request.setAttribute("requestId", requestId);
            
            // Log the incoming request details
            logRequest(httpRequest);
        }
        
        // Continue the filter chain
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            throw e;
        }
        
        // TODO: Consider adding response logging capability
    }
    
    /**
     * Logs details of the HTTP request including method, URI, headers, and parameters.
     * 
     * @param request the HTTP servlet request to log
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder message = new StringBuilder();
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        
        message.append("Incoming request [")
               .append(method).append("] ")
               .append(uri);
        
        // Add query string if present
        if (queryString != null) {
            message.append("?").append(queryString);
        }
        
        message.append(" from IP: ").append(remoteAddr);
        
        logger.info("{}", message.toString());
        
        // Log headers if debug is enabled to avoid performance impact in normal operation
        if (logger.isDebugEnabled()) {
            logRequestHeaders(request);
            logRequestParameters(request);
        }
    }
    
    /**
     * Logs the headers of the HTTP request.
     * 
     * @param request the HTTP servlet request containing headers
     */
    private void logRequestHeaders(HttpServletRequest request) {
        StringBuilder headerLog = new StringBuilder("Request headers: {");
        Enumeration<String> headerNames = request.getHeaderNames();
        boolean first = true;
        
        while (headerNames.hasMoreElements()) {
            if (!first) {
                headerLog.append(", ");
            }
            first = false;
            
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // FIXME: Should mask sensitive headers like Authorization
            headerLog.append(headerName).append("=").append(headerValue);
        }
        
        headerLog.append("}");
        logger.debug("{}", headerLog.toString());
    }
    
    /**
     * Logs the parameters of the HTTP request.
     * 
     * @param request the HTTP servlet request containing parameters
     */
    private void logRequestParameters(HttpServletRequest request) {
        StringBuilder paramLog = new StringBuilder("Request parameters: {");
        Enumeration<String> paramNames = request.getParameterNames();
        boolean first = true;
        
        while (paramNames.hasMoreElements()) {
            if (!first) {
                paramLog.append(", ");
            }
            first = false;
            
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            
            paramLog.append(paramName).append("=");
            
            if (paramValues.length == 1) {
                // FIXME: Should mask sensitive parameters
                paramLog.append(paramValues[0]);
            } else {
                paramLog.append("[");
                for (int i = 0; i < paramValues.length; i++) {
                    if (i > 0) {
                        paramLog.append(", ");
                    }
                    paramLog.append(paramValues[i]);
                }
                paramLog.append("]");
            }
        }
        
        paramLog.append("}");
        logger.debug("{}", paramLog.toString());
    }
    
    @Override
    public void destroy() {
        logger.info("Destroying Request Logging Filter");
    }
}