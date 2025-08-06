package com.gradle80.mapper;

import com.gradle80.entity.Notification;
import com.gradle80.model.NotificationRequest;
import com.gradle80.model.NotificationResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper interface for converting between notification entities and DTOs.
 */
@Component
public interface NotificationMapper {
    
    /**
     * Converts a notification request to a notification entity.
     *
     * @param request the notification request
     * @return the notification entity
     */
    Notification requestToEntity(NotificationRequest request);
    
    /**
     * Converts a notification entity to a notification response.
     *
     * @param entity the notification entity
     * @return the notification response
     */
    NotificationResponse entityToResponse(Notification entity);
}