package com.gradle80.entity;

import jakarta.persistence.*;

/**
 * Entity representing a notification in the system.
 * 
 * This entity maps to the notification table in the database and
 * represents notifications sent to users.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private boolean read = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private java.util.Date createdAt;

    /**
     * Default constructor
     */
    public Notification() {
        // Required by JPA
        this.createdAt = new java.util.Date();
    }

    /**
     * Creates a new notification with the specified properties
     *
     * @param type the notification type
     * @param message the notification message
     * @param userId the ID of the user to notify
     */
    public Notification(String type, String message, Long userId) {
        this.type = type;
        this.message = message;
        this.userId = userId;
        this.createdAt = new java.util.Date();
    }

    /**
     * Gets the notification ID
     *
     * @return the notification ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the notification ID
     *
     * @param id the notification ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the notification type
     *
     * @return the notification type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the notification type
     *
     * @param type the notification type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the notification message
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the notification message
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the user ID
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID
     *
     * @param userId the user ID to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Checks if the notification has been read
     *
     * @return true if the notification has been read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status
     *
     * @param read the read status to set
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Gets the creation date
     *
     * @return the creation date
     */
    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation date
     *
     * @param createdAt the creation date to set
     */
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Marks the notification as read
     */
    public void markAsRead() {
        this.read = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;

        Notification that = (Notification) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
}