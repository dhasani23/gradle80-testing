package com.gradle80.api.dto;

import java.util.Date;
import java.util.Objects;

/**
 * Data transfer object for notification information.
 * 
 * This class represents notification data that can be transferred between
 * different layers of the application or across system boundaries.
 */
public class NotificationDto {

    private Long id;
    private Long userId;
    private String type;
    private String message;
    private boolean read;
    private Date createdAt;

    /**
     * Default constructor for NotificationDto.
     */
    public NotificationDto() {
        // Default constructor for serialization frameworks
    }

    /**
     * Full constructor for NotificationDto.
     *
     * @param id        The notification identifier
     * @param userId    The user identifier
     * @param type      The notification type
     * @param message   The notification message
     * @param read      The read status
     * @param createdAt The creation timestamp
     */
    public NotificationDto(Long id, Long userId, String type, String message, boolean read, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    /**
     * Gets the notification identifier.
     *
     * @return The notification identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the notification identifier.
     *
     * @param id The notification identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user identifier.
     *
     * @return The user identifier
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId The user identifier
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the notification type.
     *
     * @return The notification type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the notification type.
     *
     * @param type The notification type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the notification message.
     *
     * @return The notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the notification message.
     *
     * @param message The notification message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the read status.
     *
     * @return The read status
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status.
     *
     * @param read The read status
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return The creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDto that = (NotificationDto) o;
        return read == that.read &&
                Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(message, that.message) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, message, read, createdAt);
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * Create a new builder instance
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder pattern implementation for NotificationDto
     */
    public static class Builder {
        private Long id;
        private Long userId;
        private String type;
        private String message;
        private boolean read;
        private Date createdAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder read(boolean read) {
            this.read = read;
            return this;
        }
        
        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public NotificationDto build() {
            return new NotificationDto(id, userId, type, message, read, createdAt);
        }
    }
}