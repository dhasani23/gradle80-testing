package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Authentication request model used for user login.
 * This request contains credentials needed for authenticating users.
 */
public class AuthenticationRequest extends ApiRequest {
    
    /**
     * User name for authentication
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * User password for authentication
     */
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    /**
     * Default constructor
     */
    public AuthenticationRequest() {
        super();
    }
    
    /**
     * Constructor with username and password
     * 
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public AuthenticationRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
    
    /**
     * Get username
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Set username
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get password
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Set password
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Validates this authentication request
     * Ensures that both username and password are not empty and meet
     * minimum complexity requirements
     * 
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First validate the base request
        if (!super.validate()) {
            return false;
        }
        
        // Validate username
        if (username == null || username.trim().isEmpty() || username.length() < 3) {
            return false;
        }
        
        // Validate password - at least 6 characters
        // FIXME: Implement stronger password validation with special characters requirement
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            return false;
        }
        
        // TODO: Add additional security checks like brute force prevention
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(username, that.username) &&
               // Note: We compare password existence, not value, for security reasons in equals
               (password == null) == (that.password == null);
    }
    
    @Override
    public int hashCode() {
        // Note: We use constant value for password in hash calculation for security
        return Objects.hash(super.hashCode(), username, password != null ? "hasPassword" : null);
    }
    
    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "requestId='" + getRequestId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}