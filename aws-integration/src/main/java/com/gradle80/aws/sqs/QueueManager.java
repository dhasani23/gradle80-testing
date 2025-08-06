package com.gradle80.aws.sqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing SQS queue operations.
 * Provides functionality to create, delete, purge queues and retrieve queue attributes.
 */
@Service
@Slf4j
public class QueueManager {

    /**
     * SQS client for performing queue operations
     */
    private final AmazonSQS sqsClient;

    /**
     * Constructs a QueueManager with the specified SQS client.
     *
     * @param sqsClient The Amazon SQS client
     */
    @Autowired
    public QueueManager(AmazonSQS sqsClient) {
        this.sqsClient = sqsClient;
    }

    /**
     * Creates a new SQS queue with the specified name.
     *
     * @param queueName The name of the queue to create
     * @return The URL of the created queue
     */
    public String createQueue(String queueName) {
        log.info("Creating queue with name: {}", queueName);
        
        try {
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
            CreateQueueResult createQueueResult = sqsClient.createQueue(createQueueRequest);
            String queueUrl = createQueueResult.getQueueUrl();
            
            log.info("Successfully created queue. Queue URL: {}", queueUrl);
            return queueUrl;
        } catch (Exception e) {
            log.error("Failed to create queue: {}", queueName, e);
            throw e;
        }
    }

    /**
     * Deletes an SQS queue specified by its URL.
     *
     * @param queueUrl The URL of the queue to delete
     * @throws QueueDoesNotExistException if the queue does not exist
     */
    public void deleteQueue(String queueUrl) {
        log.info("Deleting queue: {}", queueUrl);
        
        try {
            sqsClient.deleteQueue(queueUrl);
            log.info("Successfully deleted queue: {}", queueUrl);
        } catch (QueueDoesNotExistException e) {
            log.error("Queue does not exist: {}", queueUrl);
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete queue: {}", queueUrl, e);
            throw e;
        }
    }

    /**
     * Retrieves attributes of an SQS queue.
     *
     * @param queueUrl The URL of the queue
     * @return A map of attribute name-value pairs
     */
    public Map<String, String> getQueueAttributes(String queueUrl) {
        log.info("Getting attributes for queue: {}", queueUrl);
        
        try {
            // Request all attributes
            GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                .withQueueUrl(queueUrl)
                .withAttributeNames("All");
                
            GetQueueAttributesResult result = sqsClient.getQueueAttributes(request);
            
            // Convert the attribute map to a simple String to String map
            Map<String, String> attributes = new HashMap<>();
            result.getAttributes().forEach((key, value) -> attributes.put(key, value));
            
            log.debug("Retrieved {} attributes for queue: {}", attributes.size(), queueUrl);
            return attributes;
        } catch (Exception e) {
            log.error("Failed to get attributes for queue: {}", queueUrl, e);
            throw e;
        }
    }

    /**
     * Purges all messages from an SQS queue.
     *
     * @param queueUrl The URL of the queue to purge
     */
    public void purgeQueue(String queueUrl) {
        log.info("Purging queue: {}", queueUrl);
        
        try {
            PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest(queueUrl);
            sqsClient.purgeQueue(purgeQueueRequest);
            log.info("Successfully purged queue: {}", queueUrl);
            
            // TODO: Consider implementing a delay check after purge due to SQS purge request limitations
        } catch (Exception e) {
            log.error("Failed to purge queue: {}", queueUrl, e);
            throw e;
        }
    }
    
    // FIXME: Queue creation with attributes and FIFO queues needs additional implementation
}