package com.gradle80.data.repository;

import com.gradle80.data.entity.NotificationEntity;
import com.gradle80.data.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for notification operations.
 * Provides methods for searching and filtering notifications in the database.
 * Extends the CustomJpaRepository to inherit common repository functionality.
 */
@Repository
public interface NotificationRepository extends CustomJpaRepository<NotificationEntity, Long> {

    /**
     * Find all notifications for a specific user.
     * 
     * @param user the user whose notifications to retrieve
     * @return a list of all notifications for the user
     */
    List<NotificationEntity> findByUser(UserEntity user);
    
    /**
     * Find notifications for a specific user filtered by read status.
     * 
     * @param user the user whose notifications to retrieve
     * @param read the read status to filter by (true for read notifications, false for unread)
     * @return a list of notifications matching the criteria
     */
    List<NotificationEntity> findByUserAndRead(UserEntity user, boolean read);
    
    /**
     * Count notifications for a specific user filtered by read status.
     * Particularly useful for counting unread notifications.
     * 
     * @param user the user whose notifications to count
     * @param read the read status to filter by (true for read notifications, false for unread)
     * @return the count of notifications matching the criteria
     */
    int countByUserAndRead(UserEntity user, boolean read);
    
    // TODO: Add method to find notifications by type
    
    /**
     * Find recent notifications for a specific user.
     * This is a custom query that could be implemented in a custom repository implementation.
     * 
     * @param user the user whose notifications to retrieve
     * @param days the number of days to look back
     * @return a list of recent notifications for the user
     */
    // List<NotificationEntity> findRecentByUser(UserEntity user, int days);
    
    // FIXME: Consider adding batch operations for marking multiple notifications as read/unread
}