package com.gradle80.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity class for notifications.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The notification ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user ID the notification is for.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * The notification type.
     */
    @Column(nullable = false)
    private String type;
    
    /**
     * The notification title.
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * The notification message content.
     */
    @Column(nullable = false, length = 1000)
    private String message;
    
    /**
     * Whether the notification has been read.
     */
    @Column(nullable = false)
    private boolean read;
    
    /**
     * When the notification was created (timestamp).
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    /**
     * When the notification was read (timestamp).
     */
    @Column(name = "read_at")
    private Long readAt;
    
    /**
     * External ID (e.g., SNS message ID).
     */
    @Column(name = "external_id")
    private String externalId;
    
    /**
     * Additional attributes stored as JSON.
     */
    @Column(columnDefinition = "TEXT")
    private String attributesJson;
}