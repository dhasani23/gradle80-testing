package com.gradle80.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages SNS topic operations
 * 
 * This class provides functionality to create, delete, subscribe to, 
 * and unsubscribe from Amazon SNS topics.
 */
public class TopicManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TopicManager.class);
    
    private final AmazonSNS snsClient;
    
    /**
     * Constructs a TopicManager with the specified SNS client
     * 
     * @param snsClient the Amazon SNS client
     */
    public TopicManager(AmazonSNS snsClient) {
        if (snsClient == null) {
            throw new IllegalArgumentException("SNS client cannot be null");
        }
        this.snsClient = snsClient;
    }
    
    /**
     * Creates a new SNS topic with the given name
     * 
     * @param topicName the name of the topic to create
     * @return the ARN of the created topic
     * @throws RuntimeException if the topic creation fails
     */
    public String createTopic(String topicName) {
        if (topicName == null || topicName.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic name cannot be null or empty");
        }
        
        logger.info("Creating SNS topic: {}", topicName);
        try {
            CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
            CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
            
            String topicArn = createTopicResult.getTopicArn();
            logger.info("Successfully created SNS topic: {} with ARN: {}", topicName, topicArn);
            
            return topicArn;
        } catch (Exception e) {
            logger.error("Failed to create SNS topic: {}", topicName, e);
            throw new RuntimeException("Failed to create SNS topic: " + topicName, e);
        }
    }
    
    /**
     * Deletes an SNS topic with the given ARN
     * 
     * @param topicArn the ARN of the topic to delete
     * @throws RuntimeException if the topic deletion fails
     */
    public void deleteTopic(String topicArn) {
        if (topicArn == null || topicArn.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic ARN cannot be null or empty");
        }
        
        logger.info("Deleting SNS topic with ARN: {}", topicArn);
        try {
            DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
            snsClient.deleteTopic(deleteTopicRequest);
            logger.info("Successfully deleted SNS topic with ARN: {}", topicArn);
        } catch (Exception e) {
            logger.error("Failed to delete SNS topic with ARN: {}", topicArn, e);
            throw new RuntimeException("Failed to delete SNS topic with ARN: " + topicArn, e);
        }
    }
    
    /**
     * Subscribes an endpoint to an SNS topic
     * 
     * @param topicArn the ARN of the topic to subscribe to
     * @param protocol the protocol to use (e.g., "email", "sms", "http", "https", "lambda", "sqs")
     * @param endpoint the endpoint that will receive the notifications
     * @return the ARN of the subscription
     * @throws RuntimeException if the subscription fails
     */
    public String subscribeToTopic(String topicArn, String protocol, String endpoint) {
        if (topicArn == null || topicArn.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic ARN cannot be null or empty");
        }
        if (protocol == null || protocol.trim().isEmpty()) {
            throw new IllegalArgumentException("Protocol cannot be null or empty");
        }
        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("Endpoint cannot be null or empty");
        }
        
        logger.info("Subscribing {} endpoint {} to topic {}", protocol, endpoint, topicArn);
        try {
            SubscribeRequest subscribeRequest = new SubscribeRequest()
                    .withTopicArn(topicArn)
                    .withProtocol(protocol)
                    .withEndpoint(endpoint);
            
            SubscribeResult subscribeResult = snsClient.subscribe(subscribeRequest);
            String subscriptionArn = subscribeResult.getSubscriptionArn();
            
            logger.info("Successfully subscribed {} endpoint {} to topic {} with subscription ARN: {}",
                    protocol, endpoint, topicArn, subscriptionArn);
            
            // FIXME: For email protocol, the returned subscriptionArn will be "pending confirmation"
            // Need to handle this case specifically
            
            return subscriptionArn;
        } catch (Exception e) {
            logger.error("Failed to subscribe {} endpoint {} to topic {}", protocol, endpoint, topicArn, e);
            throw new RuntimeException(
                    String.format("Failed to subscribe %s endpoint %s to topic %s", protocol, endpoint, topicArn), e);
        }
    }
    
    /**
     * Unsubscribes from an SNS topic using the subscription ARN
     * 
     * @param subscriptionArn the ARN of the subscription to cancel
     * @throws RuntimeException if the unsubscribe operation fails
     */
    public void unsubscribeFromTopic(String subscriptionArn) {
        if (subscriptionArn == null || subscriptionArn.trim().isEmpty()) {
            throw new IllegalArgumentException("Subscription ARN cannot be null or empty");
        }
        
        logger.info("Unsubscribing from subscription with ARN: {}", subscriptionArn);
        try {
            UnsubscribeRequest unsubscribeRequest = new UnsubscribeRequest(subscriptionArn);
            snsClient.unsubscribe(unsubscribeRequest);
            logger.info("Successfully unsubscribed from subscription with ARN: {}", subscriptionArn);
        } catch (Exception e) {
            logger.error("Failed to unsubscribe from subscription with ARN: {}", subscriptionArn, e);
            throw new RuntimeException("Failed to unsubscribe from subscription with ARN: " + subscriptionArn, e);
        }
    }
    
    // TODO: Add methods for publishing messages to topics
    // TODO: Add methods for listing all subscriptions for a topic
    // TODO: Add methods for checking if a topic exists
}