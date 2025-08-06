package com.gradle80.service.domain;

import java.util.Date;

/**
 * Domain model representing a user in the system.
 * This class acts as an intermediary between the data layer's UserEntity and 
 * the API layer's UserDto.
 */
public class User {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Default constructor
     */
    public User() {
        // Default constructor
    }
    
    /**
     * Constructs a new User with the specified properties.
     *
     * @param id        the user identifier
     * @param username  the username
     * @param email     the email address
     * @param firstName the first name
     * @param lastName  the last name
     * @param active    the active status
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public User(Long id, String username, String email, String firstName, 
                String lastName, boolean active, Date createdAt, Date updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * @return the user identifier
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the user identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * @return the active status
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * @param active the active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Returns the user's full name (first name + last name)
     * 
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * Builder pattern implementation for User
     */
    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean active = true;
        private Date createdAt;
        private Date updatedAt;
        
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
        
        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public User build() {
            return new User(id, username, email, firstName, lastName, active, createdAt, updatedAt);
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