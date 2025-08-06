package com.gradle80.service.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AWS SNS service client.
 * 
 * This class provides functionality to publish messages to Amazon SNS topics.
 * It handles the communication with the AWS SNS service.
 */
@Service
public class SnsClient {

    private static final Logger logger = LoggerFactory.getLogger(SnsClient.class);
    
    private final AmazonSNS snsClient;
    private final String topicArn;

    /**
     * Constructs an SnsClient with the required dependencies.
     *
     * @param snsClient the AmazonSNS client
     * @param topicArn the SNS topic ARN to publish messages to
     */
    @Autowired
    public SnsClient(AmazonSNS snsClient, @Value("${aws.sns.topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
        logger.info("Initialized SNS client for topic: {}", topicArn);
    }

    /**
     * Publishes a message to the configured SNS topic.
     *
     * @param subject the subject of the message
     * @param message the message body
     * @return the message ID from the publish result
     */
    public String publishMessage(String subject, String message) {
        try {
            logger.debug("Publishing message with subject: {} to topic: {}", subject, topicArn);
            
            PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withSubject(subject)
                .withMessage(message);
            
            PublishResult publishResult = snsClient.publish(publishRequest);
            
            logger.info("Message published. Message ID: {}", publishResult.getMessageId());
            
            return publishResult.getMessageId();
        } catch (Exception e) {
            logger.error("Failed to publish message to SNS topic: {}", topicArn, e);
            // TODO: Implement retry mechanism for transient failures
            // FIXME: Consider using a circuit breaker pattern for handling AWS service outages
            throw new RuntimeException("Failed to publish message to SNS", e);
        }
    }
    
    /**
     * Gets the topic ARN used by this client.
     *
     * @return the configured SNS topic ARN
     */
    public String getTopicArn() {
        return this.topicArn;
    }
}