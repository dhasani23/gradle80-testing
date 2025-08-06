package com.gradle80.api.request;

import com.gradle80.api.model.ApiRequest;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * User creation/update request.
 * This class contains the data required to create or update a user.
 */
public class UserRequest extends ApiRequest {
    
    /**
     * User name
     */
    private String username;
    
    /**
     * Email address
     */
    private String email;
    
    /**
     * First name
     */
    private String firstName;
    
    /**
     * Last name
     */
    private String lastName;
    
    /**
     * User password
     */
    private String password;
    
    /**
     * Default constructor
     */
    public UserRequest() {
        super();
    }
    
    /**
     * Constructor with all fields
     * 
     * @param username the user name
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     * @param password the user password
     */
    public UserRequest(String username, String email, String firstName, String lastName, String password) {
        super();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    
    /**
     * Gets the username
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the email
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the first name
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Validates the request
     * Checks that:
     * - Base validation passes
     * - Username is not null or empty
     * - Email is valid format
     * - Password meets minimum requirements
     * 
     * @return true if the request is valid, false otherwise
     */
    @Override
    public boolean validate() {
        // First check the base validation
        if (!super.validate()) {
            return false;
        }
        
        // Check that required fields are present
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Validate email format using simple regex
        // FIXME: Use a more comprehensive email validation regex in production
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }
        
        // Check password strength (at least 8 characters)
        // TODO: Implement stronger password validation with special chars, numbers, etc.
        if (password.length() < 8) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "UserRequest{" +
                "requestId='" + getRequestId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}