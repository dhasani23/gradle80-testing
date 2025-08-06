package com.gradle80.aws.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.aws.sqs.model.SqsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of the MessageProcessor interface.
 * 
 * This class provides standard message processing capabilities including
 * JSON deserialization of message content to specified target types.
 * 
 * @since 1.0
 */
@Service
public class DefaultMessageProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);
    
    /**
     * JSON object mapper for deserialization of message content
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates a new DefaultMessageProcessor with the provided ObjectMapper.
     *
     * @param objectMapper the Jackson ObjectMapper for JSON processing
     */
    @Autowired
    public DefaultMessageProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation logs the message details and attempts basic processing.
     * For more specific handling, extend this class or implement a custom MessageProcessor.
     */
    @Override
    public void processMessage(SqsMessage message) {
        if (message == null) {
            logger.warn("Received null message, ignoring");
            return;
        }

        logger.info("Processing message: {}", message.getMessageId());
        logger.debug("Message content: {}", message.getBody());
        
        try {
            // TODO: Implement specific message handling logic based on message attributes
            // This method should be extended by subclasses to handle specific message types
            
            // FIXME: Currently only logs the message, needs business logic implementation
            
            // Example of checking message type attribute and routing accordingly
            if (message.hasAttribute("messageType")) {
                String messageType = message.getAttribute("messageType");
                logger.info("Message type: {}", messageType);
                
                // Route based on message type
                // switch(messageType) { ... }
            }
        } catch (Exception e) {
            logger.error("Failed to process message {}: {}", message.getMessageId(), e.getMessage(), e);
            throw new RuntimeException("Message processing failed", e);
        }
    }

    /**
     * Converts a message body to the specified target class using JSON deserialization.
     *
     * @param <T> the target type
     * @param message the message containing JSON content
     * @param targetClass the class to deserialize to
     * @return the deserialized object
     * @throws IllegalArgumentException if the message body is invalid JSON or cannot be converted
     */
    public <T> T convertMessageToType(SqsMessage message, Class<T> targetClass) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        if (targetClass == null) {
            throw new IllegalArgumentException("Target class cannot be null");
        }

        String body = message.getBody();
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body is empty");
        }

        try {
            logger.debug("Converting message {} to type {}", message.getMessageId(), targetClass.getName());
            return objectMapper.readValue(body, targetClass);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert message {} to {}: {}", message.getMessageId(), 
                    targetClass.getName(), e.getMessage());
            throw new IllegalArgumentException("Failed to parse message body as " + 
                    targetClass.getSimpleName(), e);
        }
    }
}