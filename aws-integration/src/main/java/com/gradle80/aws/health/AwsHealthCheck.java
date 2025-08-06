package com.gradle80.aws.health;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service responsible for checking health status of AWS services.
 * Provides methods to check connectivity and functionality of SQS and SNS services.
 */
@Service
public class AwsHealthCheck {
    
    private static final Logger logger = LoggerFactory.getLogger(AwsHealthCheck.class);
    
    /**
     * SQS client for interacting with Amazon Simple Queue Service
     */
    private final AmazonSQS sqsClient;
    
    /**
     * SNS client for interacting with Amazon Simple Notification Service
     */
    private final AmazonSNS snsClient;
    
    /**
     * Cached health status to avoid excessive AWS calls
     */
    private HealthStatus cachedStatus;
    
    /**
     * Time in milliseconds when the cache should be considered stale
     */
    private static final long CACHE_TTL_MS = 60000; // 1 minute
    
    /**
     * Constructs an AWS Health Check service with the required clients
     * 
     * @param sqsClient Amazon SQS client
     * @param snsClient Amazon SNS client
     */
    @Autowired
    public AwsHealthCheck(AmazonSQS sqsClient, AmazonSNS snsClient) {
        this.sqsClient = sqsClient;
        this.snsClient = snsClient;
        this.cachedStatus = null;
    }
    
    /**
     * Checks if the SQS service is available and functioning correctly.
     * Makes a lightweight API call to test connectivity.
     * 
     * @return true if the SQS service is healthy, false otherwise
     */
    public boolean checkSqsHealth() {
        try {
            // Making a lightweight call to check SQS service health
            sqsClient.listQueues(new ListQueuesRequest().withMaxResults(1));
            logger.debug("SQS health check passed");
            return true;
        } catch (AmazonClientException e) {
            logger.warn("SQS health check failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during SQS health check", e);
            return false;
        }
    }
    
    /**
     * Checks if the SNS service is available and functioning correctly.
     * Makes a lightweight API call to test connectivity.
     * 
     * @return true if the SNS service is healthy, false otherwise
     */
    public boolean checkSnsHealth() {
        try {
            // Making a lightweight call to check SNS service health
            snsClient.listTopics(new ListTopicsRequest());
            logger.debug("SNS health check passed");
            return true;
        } catch (AmazonClientException e) {
            logger.warn("SNS health check failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during SNS health check", e);
            return false;
        }
    }
    
    /**
     * Gets the current health status of all AWS services.
     * Uses cached results if they are recent enough, otherwise performs new checks.
     * 
     * @return a HealthStatus object representing the current health state of AWS services
     */
    public HealthStatus getHealthStatus() {
        if (isCacheValid()) {
            logger.debug("Using cached health status");
            return cachedStatus;
        }
        
        // Cache is stale or doesn't exist, perform new health checks
        boolean sqsHealthy = checkSqsHealth();
        boolean snsHealthy = checkSnsHealth();
        LocalDateTime now = LocalDateTime.now();
        
        // Update the cached status
        cachedStatus = new HealthStatus(sqsHealthy, snsHealthy, now);
        
        return cachedStatus;
    }
    
    /**
     * Determines if the cached health status is still valid
     * 
     * @return true if cache exists and is still valid, false otherwise
     */
    private boolean isCacheValid() {
        if (cachedStatus == null) {
            return false;
        }
        
        LocalDateTime lastChecked = cachedStatus.getLastChecked();
        LocalDateTime now = LocalDateTime.now();
        
        // Calculate age of cache in milliseconds
        long ageMs = java.time.Duration.between(lastChecked, now).toMillis();
        
        return ageMs < CACHE_TTL_MS;
    }
    
    /**
     * Forces a refresh of the health status cache
     * 
     * @return a fresh HealthStatus object
     */
    public HealthStatus refreshHealthStatus() {
        // Invalidate cache by setting it to null
        cachedStatus = null;
        
        // Get fresh health status
        return getHealthStatus();
    }
    
    /**
     * Performs a deep health check that tests additional functionality
     * beyond basic connectivity.
     * 
     * TODO: Implement more thorough checks such as:
     * - Queue/Topic creation and deletion
     * - Message publishing and receiving
     * - Permission verification
     * 
     * @return a detailed health status
     */
    public HealthStatus performDeepHealthCheck() {
        // FIXME: Currently just returns the standard health check
        // Should be expanded to include more comprehensive testing
        return getHealthStatus();
    }
}