package com.gradle80.api.response;

import com.gradle80.api.model.ApiResponse;

/**
 * Authentication response that provides token and expiration information
 * after a successful authentication attempt.
 */
public class AuthenticationResponse extends ApiResponse {
    
    /**
     * Authentication token that can be used for subsequent API calls.
     * This token should be included in the Authorization header for secured endpoints.
     */
    private String token;
    
    /**
     * Timestamp when the authentication token expires (in milliseconds since epoch).
     * Client applications should refresh the token before this time.
     */
    private Long expiresAt;
    
    /**
     * Default constructor.
     * Initializes a response with default values.
     */
    public AuthenticationResponse() {
        super();
    }
    
    /**
     * Constructs a new authentication response with the given parameters.
     *
     * @param success   whether the authentication was successful
     * @param message   response message
     * @param token     authentication token
     * @param expiresAt expiration timestamp
     */
    public AuthenticationResponse(boolean success, String message, String token, Long expiresAt) {
        super(success, message);
        this.token = token;
        this.expiresAt = expiresAt;
    }
    
    /**
     * Factory method to create a successful authentication response.
     *
     * @param token     authentication token
     * @param expiresAt expiration timestamp
     * @return a new AuthenticationResponse instance with success=true
     */
    public static AuthenticationResponse success(String token, Long expiresAt) {
        return new AuthenticationResponse(true, "Authentication successful", token, expiresAt);
    }
    
    /**
     * Factory method to create a failed authentication response.
     *
     * @param message the error message
     * @return a new AuthenticationResponse instance with success=false
     */
    public static AuthenticationResponse failure(String message) {
        return new AuthenticationResponse(false, message, null, null);
    }
    
    /**
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }
    
    /**
     * @param token the authentication token
     */
    public void setToken(String token) {
        this.token = token;
    }
    
    /**
     * @return the expiration timestamp
     */
    public Long getExpiresAt() {
        return expiresAt;
    }
    
    /**
     * @param expiresAt the expiration timestamp
     */
    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Checks if the token has expired.
     * 
     * @return true if the current time is after the expiration time, false otherwise
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return true;
        }
        return System.currentTimeMillis() > expiresAt;
    }
    
    /**
     * Calculates time remaining until expiration in seconds.
     * 
     * @return seconds remaining until token expiration, or 0 if already expired
     */
    public long getSecondsUntilExpiration() {
        if (expiresAt == null || isExpired()) {
            return 0;
        }
        return (expiresAt - System.currentTimeMillis()) / 1000;
    }
    
    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", token='" + (token != null ? "[PROTECTED]" : "null") + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
    
    // FIXME: Consider obfuscating or redacting the token in debug logs for security
}