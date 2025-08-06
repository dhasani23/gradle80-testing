package com.gradle80.web.context;

/**
 * Thread-local context for storing and retrieving request information.
 * Provides access to request-specific data throughout the request lifecycle
 * without having to pass the data through method parameters.
 * 
 * This class uses ThreadLocal to store context information that is specific
 * to the current request thread.
 */
public class RequestContext {

    /**
     * ThreadLocal variable that holds the RequestContext for the current thread.
     */
    private static final ThreadLocal<RequestContext> currentContext = new ThreadLocal<>();
    
    /**
     * Unique identifier for the request.
     */
    private String requestId;
    
    /**
     * ID of the user making the request, or null if not authenticated.
     */
    private Long userId;
    
    /**
     * Timestamp when the request started processing.
     */
    private long startTime;

    /**
     * Creates a new RequestContext with specified values.
     *
     * @param requestId Unique identifier for the request
     * @param userId ID of the user making the request (can be null)
     * @param startTime Timestamp when the request started
     */
    public RequestContext(String requestId, Long userId, long startTime) {
        this.requestId = requestId;
        this.userId = userId;
        this.startTime = startTime;
    }

    /**
     * Default constructor.
     */
    public RequestContext() {
        // Default constructor for when parameters are not available immediately
    }

    /**
     * Gets the current RequestContext from the ThreadLocal storage.
     * If no context exists for the current thread, creates a new one.
     *
     * @return The current RequestContext for this thread
     */
    public static RequestContext getCurrentContext() {
        RequestContext context = currentContext.get();
        if (context == null) {
            // FIXME: Consider if automatic initialization is appropriate or if we should throw an exception
            context = new RequestContext();
            currentContext.set(context);
        }
        return context;
    }

    /**
     * Sets the current RequestContext in the ThreadLocal storage.
     *
     * @param context The RequestContext to set for the current thread
     */
    public static void setCurrentContext(RequestContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        currentContext.set(context);
    }

    /**
     * Clears the current RequestContext from the ThreadLocal storage.
     * This method should be called at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        currentContext.remove();
    }

    /**
     * Gets the request identifier.
     *
     * @return The request identifier
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request identifier.
     *
     * @param requestId The request identifier to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the user identifier.
     *
     * @return The user identifier or null if not set
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId The user identifier to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the request start time.
     *
     * @return The request start time in milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the request start time.
     *
     * @param startTime The request start time to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Calculates the elapsed time since the request started.
     *
     * @return The elapsed time in milliseconds
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "requestId='" + requestId + '\'' +
                ", userId=" + userId +
                ", startTime=" + startTime +
                '}';
    }
    
    // TODO: Add additional context fields as needed (e.g., locale, timezone)
    // TODO: Consider adding validation for required fields
}