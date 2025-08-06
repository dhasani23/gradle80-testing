package com.gradle80.client.implementation;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.gradle80.client.SnsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the SnsClient interface using AWS SDK.
 */
@Component
public class SnsClientImpl implements SnsClient {

    private static final Logger logger = LoggerFactory.getLogger(SnsClientImpl.class);
    
    private final AmazonSNS amazonSNS;
    
    /**
     * Constructor for dependency injection.
     *
     * @param amazonSNS AWS SNS client from AWS SDK
     */
    @Autowired
    public SnsClientImpl(AmazonSNS amazonSNS) {
        this.amazonSNS = amazonSNS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String publishMessage(String topicArn, String message, Map<String, String> attributes) {
        try {
            logger.debug("Publishing message to SNS topic: {}", topicArn);
            
            PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message)
                .withMessageAttributes(convertToMessageAttributes(attributes));
            
            PublishResult result = amazonSNS.publish(publishRequest);
            
            logger.debug("Message published successfully with ID: {}", result.getMessageId());
            return result.getMessageId();
        } catch (Exception e) {
            logger.error("Error publishing message to SNS", e);
            // TODO: Add retry mechanism for transient failures
            return null;
        }
    }
    
    /**
     * Converts a map of string attributes to SNS MessageAttributeValue objects.
     *
     * @param attributes the string attributes map
     * @return map of SNS message attributes
     */
    private Map<String, MessageAttributeValue> convertToMessageAttributes(Map<String, String> attributes) {
        if (attributes == null) {
            return new HashMap<>();
        }
        
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        
        attributes.forEach((key, value) -> {
            if (value != null) {
                MessageAttributeValue attributeValue = new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(value);
                messageAttributes.put(key, attributeValue);
            }
        });
        
        return messageAttributes;
    }
}