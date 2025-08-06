package com.gradle80.mapper.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.entity.Notification;
import com.gradle80.mapper.NotificationMapper;
import com.gradle80.model.NotificationRequest;
import com.gradle80.model.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the NotificationMapper interface.
 */
@Component
public class NotificationMapperImpl implements NotificationMapper {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMapperImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification requestToEntity(NotificationRequest request) {
        if (request == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        
        // Convert attributes map to JSON string
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            try {
                notification.setAttributesJson(objectMapper.writeValueAsString(request.getAttributes()));
            } catch (JsonProcessingException e) {
                logger.error("Error serializing attributes to JSON", e);
                // FIXME: Improve error handling for JSON serialization errors
            }
        }

        return notification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationResponse entityToResponse(Notification entity) {
        if (entity == null) {
            return null;
        }

        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setType(entity.getType());
        response.setTitle(entity.getTitle());
        response.setMessage(entity.getMessage());
        response.setRead(entity.isRead());
        response.setCreatedAt(entity.getCreatedAt());
        response.setReadAt(entity.getReadAt());
        response.setExternalId(entity.getExternalId());
        
        // Convert JSON string to attributes map
        if (entity.getAttributesJson() != null && !entity.getAttributesJson().isEmpty()) {
            try {
                Map<String, String> attributes = objectMapper.readValue(entity.getAttributesJson(), 
                        objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class));
                response.setAttributes(attributes);
            } catch (IOException e) {
                logger.error("Error deserializing JSON to attributes map", e);
                // TODO: Consider how to handle partial data in case of deserialization errors
                response.setAttributes(new HashMap<>());
            }
        }

        return response;
    }
}