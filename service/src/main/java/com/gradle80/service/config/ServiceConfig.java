package com.gradle80.service.config;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.gradle80.service.aws.SnsClient;
import com.gradle80.service.aws.SqsClient;
import com.gradle80.service.cache.CacheManager;
import com.gradle80.service.cache.InMemoryCacheManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class for the service module.
 * 
 * This class provides beans for:
 * - Cache management
 * - AWS integration (SNS and SQS)
 * - Security configuration
 */
@Configuration
public class ServiceConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.sns.topic.arn:}")
    private String snsTopicArn;
    
    @Value("${aws.sqs.queue.url:}")
    private String sqsQueueUrl;
    
    @Value("${cache.default.ttl:3600}")
    private long defaultCacheTtl;

    /**
     * Creates a cache manager bean for application-wide caching.
     * Uses an in-memory implementation with a concurrent hash map.
     *
     * @return configured CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        // Using in-memory cache implementation with default TTL from properties
        // TODO: Consider adding support for distributed caching in the future
        return new InMemoryCacheManager(new ConcurrentHashMap<>(), defaultCacheTtl);
    }

    /**
     * Creates an SNS client for publishing notifications to AWS SNS topics.
     *
     * @return configured SnsClient instance
     */
    @Bean
    public SnsClient snsClient() {
        // FIXME: Proper configuration for credentials should be used in production
        AmazonSNS amazonSNS = AmazonSNSClient.builder()
                .withRegion(awsRegion)
                .build();
        
        return new SnsClient(amazonSNS, snsTopicArn);
    }

    /**
     * Creates an SQS client for sending and receiving messages using AWS SQS queues.
     *
     * @return configured SqsClient instance
     */
    @Bean
    public SqsClient sqsClient() {
        // FIXME: Proper configuration for credentials should be used in production
        AmazonSQS amazonSQS = AmazonSQSClient.builder()
                .withRegion(awsRegion)
                .build();
        
        return new SqsClient(amazonSQS, sqsQueueUrl);
    }

    /**
     * Creates a password encoder for secure password hashing and verification.
     * Uses BCrypt implementation with default strength (10).
     *
     * @return configured PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Using BCrypt with default strength (10)
        return new BCryptPasswordEncoder();
    }
}