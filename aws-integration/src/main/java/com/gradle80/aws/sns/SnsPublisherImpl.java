package com.gradle80.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the SnsPublisher interface for publishing messages to Amazon SNS topics.
 * <p>
 * This service handles the serialization of message objects to JSON and communicates
 * with Amazon SNS to publish messages. It supports both simple publishing and
 * publishing with message attributes.
 * </p>
 */
@Service
public class SnsPublisherImpl implements SnsPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SnsPublisherImpl.class);
    
    /**
     * SNS Client for interacting with the Amazon SNS service
     */
    private final AmazonSNS snsClient;
    
    /**
     * JSON Mapper for serializing objects to JSON format
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new SnsPublisherImpl with the specified dependencies.
     *
     * @param snsClient The Amazon SNS client
     * @param objectMapper The JSON object mapper for serialization
     */
    @Autowired
    public SnsPublisherImpl(AmazonSNS snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublishResult publishMessage(String topicArn, Object message) {
        logger.debug("Publishing message to topic: {}", topicArn);
        
        try {
            String messageJson = serializeToJson(message);
            PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(messageJson);
            
            PublishResult result = snsClient.publish(publishRequest);
            logger.debug("Message published with ID: {}", result.getMessageId());
            return result;
        } catch (Exception e) {
            logger.error("Failed to publish message to topic {}: {}", topicArn, e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublishResult publishMessageWithAttributes(String topicArn, Object message, Map<String, String> attributes) {
        logger.debug("Publishing message with attributes to topic: {}", topicArn);
        
        try {
            String messageJson = serializeToJson(message);
            PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(messageJson);
                
            // Convert string attributes to SNS message attributes
            if (attributes != null && !attributes.isEmpty()) {
                Map<String, MessageAttributeValue> messageAttributes = convertToMessageAttributes(attributes);
                publishRequest.withMessageAttributes(messageAttributes);
            }
            
            PublishResult result = snsClient.publish(publishRequest);
            logger.debug("Message published with ID: {} and {} attributes", 
                        result.getMessageId(), 
                        attributes != null ? attributes.size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("Failed to publish message with attributes to topic {}: {}", topicArn, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Converts a map of string attributes to SNS MessageAttributeValue objects.
     *
     * @param attributes Map of string attribute name/value pairs
     * @return Map of MessageAttributeValue objects
     */
    private Map<String, MessageAttributeValue> convertToMessageAttributes(Map<String, String> attributes) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        
        attributes.forEach((key, value) -> {
            if (value != null) {
                MessageAttributeValue attributeValue = new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(value);
                messageAttributes.put(key, attributeValue);
            } else {
                logger.warn("Skipping null attribute value for key: {}", key);
            }
        });
        
        return messageAttributes;
    }
    
    /**
     * Serializes an object to JSON string.
     *
     * @param object The object to serialize
     * @return JSON representation of the object
     * @throws RuntimeException if serialization fails
     */
    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message to JSON: {}", e.getMessage());
            // TODO: Consider implementing a fallback serialization strategy
            throw new RuntimeException("Failed to serialize message to JSON", e);
        }
    }
    
    // FIXME: Add support for different message formats beyond JSON if required by clients
    
    // TODO: Implement batch publishing functionality for improved throughput
    // public List<PublishResult> publishBatch(String topicArn, List<Object> messages) {...}
}