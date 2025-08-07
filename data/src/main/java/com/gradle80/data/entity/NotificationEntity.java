package com.gradle80.data.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Notification entity representing a notification sent to a user in the system.
 * This entity maps to the 'notifications' table in the database and extends the BaseEntity
 * which provides common fields like id, created_at, and updated_at.
 * 
 * Notifications include a type, message, read status, and reference to the target user.
 */
@Entity
@Table(name = "notifications")
public class NotificationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "read", nullable = false)
    private boolean read = false;

    /**
     * Default constructor required by JPA
     */
    public NotificationEntity() {
        // Required by JPA
    }

    /**
     * Constructs a new notification entity with the specified properties.
     *
     * @param user    the target user for the notification
     * @param type    the notification type
     * @param message the notification message
     */
    public NotificationEntity(UserEntity user, String type, String message) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.read = false;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * @return the notification type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the notification type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the notification message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the read status
     */
    public boolean isRead() {
        return read;
    }

    /**
     * @param read the read status to set
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Marks this notification as read.
     */
    public void markAsRead() {
        this.read = true;
    }

    /**
     * Marks this notification as unread.
     */
    public void markAsUnread() {
        this.read = false;
    }

    /**
     * Checks if this is a system notification.
     * System notifications typically have a type that starts with "SYSTEM_".
     *
     * @return true if this is a system notification
     */
    public boolean isSystemNotification() {
        return type != null && type.startsWith("SYSTEM_");
    }

    /**
     * Checks if this notification was created recently (within the last 24 hours).
     *
     * @return true if this notification was created within the last 24 hours
     */
    public boolean isRecent() {
        // TODO: Implement proper time-based check using a utility method
        return true;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "user=" + (user != null ? user.getUsername() : "null") +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", read=" + read +
                "} " + super.toString();
    }
}