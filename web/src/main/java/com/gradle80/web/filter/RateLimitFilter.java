package com.gradle80.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.api.response.ErrorResponse;
import com.gradle80.web.model.RateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter implementation for API rate limiting.
 * Tracks client IP addresses and restricts access when too many
 * requests are made within a specific time window.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    /**
     * Cache to store rate limit information by client IP
     */
    private final Map<String, RateLimit> rateLimitCache;
    
    /**
     * Maximum number of requests allowed per minute
     */
    private final int requestsPerMinute;
    
    /**
     * JSON object mapper for serializing error responses
     */
    private final ObjectMapper objectMapper;
    
    /**
     * Time window in milliseconds (default: 1 minute)
     */
    private static final long TIME_WINDOW_MS = 60_000;

    /**
     * Constructs a RateLimitFilter with the specified request limit and object mapper.
     *
     * @param requestsPerMinute maximum requests per minute allowed from a single IP
     * @param objectMapper      JSON object mapper for serializing error responses
     */
    public RateLimitFilter(
            @Value("${api.rate-limit.requests-per-minute:60}") int requestsPerMinute,
            ObjectMapper objectMapper) {
        this.requestsPerMinute = requestsPerMinute;
        this.objectMapper = objectMapper;
        this.rateLimitCache = new ConcurrentHashMap<>();
        
        logger.info("Rate limit filter initialized with {} requests per minute", requestsPerMinute);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
                                   
        String clientIp = getClientIp(request);
        
        // Check if request exceeds rate limit
        if (!checkRateLimit(clientIp)) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            handleRateLimitExceeded(response, clientIp);
            return;
        }
        
        // Continue with the filter chain if rate limit is not exceeded
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the client has exceeded their rate limit.
     * Implements a sliding window approach where requests are counted 
     * over a rolling time period.
     *
     * @param clientIp the client's IP address
     * @return true if the client is within their rate limit, false otherwise
     */
    protected boolean checkRateLimit(String clientIp) {
        // Get or create rate limit entry for this IP
        RateLimit rateLimit = rateLimitCache.computeIfAbsent(
            clientIp, 
            ip -> new RateLimit(System.currentTimeMillis() + TIME_WINDOW_MS)
        );
        
        // Check if we should reset the counter (time window expired)
        if (rateLimit.shouldReset()) {
            rateLimit.reset();
            rateLimit.setResetTime(System.currentTimeMillis() + TIME_WINDOW_MS);
        }
        
        // Increment the counter and check if it exceeds the limit
        int requestCount = rateLimit.incrementCount();
        
        // For logging and debugging
        if (requestCount % 10 == 0) {
            logger.debug("IP {} has made {} requests in the current window", clientIp, requestCount);
        }
        
        // Return true if within limit, false if exceeded
        return requestCount <= requestsPerMinute;
    }

    /**
     * Extracts the client IP address from the request.
     * Takes into account common headers used when behind a proxy.
     *
     * @param request the HTTP servlet request
     * @return the client's IP address as a string
     */
    protected String getClientIp(HttpServletRequest request) {
        // Try to get IP from standard headers that might be set by proxies
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // If we have multiple IPs (X-Forwarded-For can contain a chain of IPs),
        // take the first one which is typically the original client
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * Handles the rate limit exceeded case by returning a 429 Too Many Requests response.
     *
     * @param response the HTTP servlet response
     * @param clientIp the client's IP address
     * @throws IOException if an I/O error occurs during writing the response
     */
    private void handleRateLimitExceeded(HttpServletResponse response, String clientIp) throws IOException {
        // Calculate when the client can try again
        RateLimit rateLimit = rateLimitCache.get(clientIp);
        long resetTime = rateLimit != null ? rateLimit.getResetTime() : System.currentTimeMillis() + TIME_WINDOW_MS;
        long retryAfterSeconds = Math.max(1, (resetTime - System.currentTimeMillis()) / 1000);
        
        // Create error response
        ErrorResponse errorResponse = new ErrorResponse(
                "Rate limit exceeded. Please try again later.",
                "RATE_LIMIT_EXCEEDED"
        );
        errorResponse.addDetail("Too many requests from your IP address");
        errorResponse.addDetail("Try again after " + retryAfterSeconds + " seconds");
        
        // Set appropriate response headers
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        
        // Write the error response as JSON
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
    
    /**
     * FIXME: Consider using a more scalable solution like Redis for rate limiting
     * when deploying in a clustered environment where this in-memory approach won't work
     */
    
    /**
     * TODO: Add configurability for different rate limits for different API endpoints
     * based on their criticality and resource requirements
     */
    
    /**
     * TODO: Implement a whitelist mechanism for trusted IPs that should bypass rate limiting
     */
}