package com.gradle80.aws.sns.model;

import java.util.Map;

/**
 * Represents a message to be published to Amazon SNS.
 * This class encapsulates the content and attributes of an SNS notification.
 */
public class SnsMessage {
    
    private String subject;
    private String message;
    private Map<String, String> attributes;
    
    /**
     * Default constructor
     */
    public SnsMessage() {
    }
    
    /**
     * Constructs a new SNS message.
     * 
     * @param subject The message subject
     * @param message The message content
     */
    public SnsMessage(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
    
    /**
     * Constructs a new SNS message with attributes.
     * 
     * @param subject The message subject
     * @param message The message content
     * @param attributes The message attributes
     */
    public SnsMessage(String subject, String message, Map<String, String> attributes) {
        this.subject = subject;
        this.message = message;
        this.attributes = attributes;
    }
    
    /**
     * @return The message subject
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * @param subject The message subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * @return The message content
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param message The message content to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return The message attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    /**
     * @param attributes The message attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public String toString() {
        return "SnsMessage{" +
                "subject='" + subject + '\'' +
                ", message='" + (message != null ? message.substring(0, Math.min(message.length(), 100)) : "null") + "'" +
                (message != null && message.length() > 100 ? "..." : "") +
                ", attributes=" + attributes +
                '}';
    }
}