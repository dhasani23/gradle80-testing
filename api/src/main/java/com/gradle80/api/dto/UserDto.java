package com.gradle80.api.dto;

/**
 * Data transfer object for user information.
 * This class holds user details that can be transferred between different layers of the application.
 * 
 * @since 1.0
 */
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    
    /**
     * Default constructor
     */
    public UserDto() {
        // Default constructor
    }
    
    /**
     * Fully parameterized constructor
     * 
     * @param id User identifier
     * @param username User name
     * @param email Email address
     * @param firstName First name
     * @param lastName Last name
     * @param active Account status
     */
    public UserDto(Long id, String username, String email, String firstName, String lastName, boolean active) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }
    
    /**
     * Get user identifier
     * 
     * @return the user identifier
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Set user identifier
     * 
     * @param id the user identifier
     */
    public void setId(Long id) {
        this.id = id;
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
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get email address
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set email address
     * 
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get first name
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Set first name
     * 
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Get last name
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Set last name
     * 
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Check if account is active
     * 
     * @return true if the account is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Set account status
     * 
     * @param active the account status
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                '}';
    }
    
    // TODO: Add equals and hashCode methods
    
    /**
     * Builder pattern implementation for UserDto
     */
    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean active;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public UserDto build() {
            return new UserDto(id, username, email, firstName, lastName, active);
        }
    }
    
    /**
     * Create a new builder instance
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}