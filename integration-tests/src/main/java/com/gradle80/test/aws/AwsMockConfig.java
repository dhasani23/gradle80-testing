package com.gradle80.test.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * AWS mock configuration for integration testing.
 * 
 * This class provides mock implementations of AWS services using
 * local endpoints for testing purposes. These mocks are designed
 * to be used with tools like LocalStack or similar AWS emulators.
 */
@Configuration
@Profile("test")
public class AwsMockConfig {

    private static final String REGION = "us-east-1";
    
    // Default LocalStack endpoints - can be overridden via environment variables
    private static final String DEFAULT_SNS_ENDPOINT = "http://localhost:4566";
    private static final String DEFAULT_SQS_ENDPOINT = "http://localhost:4566";
    
    /**
     * Creates a mock SNS client for testing.
     * 
     * @return a mock AmazonSNS client that connects to a local endpoint
     */
    @Bean
    @Primary
    public AmazonSNS mockSnsClient() {
        String endpoint = System.getProperty("aws.sns.endpoint.url", DEFAULT_SNS_ENDPOINT);
        
        // Create a mock SNS client using anonymous credentials
        AmazonSNS mockClient = AmazonSNSClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration(endpoint, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        
        // TODO: Add pre-configuration of topics if needed
        
        return mockClient;
    }
    
    /**
     * Creates a mock SQS client for testing.
     * 
     * @return a mock AmazonSQS client that connects to a local endpoint
     */
    @Bean
    @Primary
    public AmazonSQS mockSqsClient() {
        String endpoint = System.getProperty("aws.sqs.endpoint.url", DEFAULT_SQS_ENDPOINT);
        
        // Create a mock SQS client using anonymous credentials
        AmazonSQS mockClient = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration(endpoint, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        
        // FIXME: In high-throughput test scenarios, the connection pool may need tuning
        
        return mockClient;
    }
}