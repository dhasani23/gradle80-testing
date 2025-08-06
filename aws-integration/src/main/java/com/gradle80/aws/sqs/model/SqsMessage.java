package com.gradle80.aws.sqs.model;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a message received from Amazon SQS.
 * This class provides access to the message contents and attributes.
 */
public class SqsMessage {
    
    private String messageId;
    private String receiptHandle;
    private String body;
    private Map<String, String> attributes;
    private Map<String, MessageAttributeValue> messageAttributes;
    
    /**
     * Default constructor
     */
    public SqsMessage() {
        this.attributes = new HashMap<>();
        this.messageAttributes = new HashMap<>();
    }
    
    /**
     * Constructs a new SQS message.
     * 
     * @param messageId The message ID assigned by Amazon SQS
     * @param receiptHandle The receipt handle used to delete the message
     * @param body The message body
     */
    public SqsMessage(String messageId, String receiptHandle, String body) {
        this();
        this.messageId = messageId;
        this.receiptHandle = receiptHandle;
        this.body = body;
    }
    
    /**
     * Constructs a new SQS message with attributes.
     * 
     * @param messageId The message ID assigned by Amazon SQS
     * @param receiptHandle The receipt handle used to delete the message
     * @param body The message body
     * @param attributes The SQS system attributes
     * @param messageAttributes The custom message attributes
     */
    public SqsMessage(String messageId, String receiptHandle, String body, 
                    Map<String, String> attributes, Map<String, MessageAttributeValue> messageAttributes) {
        this(messageId, receiptHandle, body);
        this.attributes = attributes != null ? attributes : new HashMap<>();
        this.messageAttributes = messageAttributes != null ? messageAttributes : new HashMap<>();
    }
    
    /**
     * @return The message ID
     */
    public String getMessageId() {
        return messageId;
    }
    
    /**
     * @param messageId The message ID to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    /**
     * @return The receipt handle
     */
    public String getReceiptHandle() {
        return receiptHandle;
    }
    
    /**
     * @param receiptHandle The receipt handle to set
     */
    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
    
    /**
     * @return The message body
     */
    public String getBody() {
        return body;
    }
    
    /**
     * @param body The message body to set
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * @return The SQS system attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    /**
     * Gets a specific attribute value.
     * 
     * @param name The attribute name
     * @return The attribute value or null if not found
     */
    public String getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }
    
    /**
     * Checks if an attribute exists.
     * 
     * @param name The attribute name
     * @return true if the attribute exists, false otherwise
     */
    public boolean hasAttribute(String name) {
        return attributes != null && attributes.containsKey(name);
    }
    
    /**
     * @param attributes The SQS system attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    /**
     * @return The custom message attributes
     */
    public Map<String, MessageAttributeValue> getMessageAttributes() {
        return messageAttributes;
    }
    
    /**
     * @param messageAttributes The custom message attributes to set
     */
    public void setMessageAttributes(Map<String, MessageAttributeValue> messageAttributes) {
        this.messageAttributes = messageAttributes;
    }
    
    @Override
    public String toString() {
        return "SqsMessage{" +
                "messageId='" + messageId + '\'' +
                ", body='" + (body != null ? body.substring(0, Math.min(body.length(), 100)) : "null") + "'" +
                (body != null && body.length() > 100 ? "..." : "") +
                ", attributes=" + attributes +
                ", messageAttributesCount=" + (messageAttributes != null ? messageAttributes.size() : 0) +
                '}';
    }
}