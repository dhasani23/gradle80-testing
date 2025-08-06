package com.gradle80.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

/**
 * Configuration class for AWS integration services.
 * Provides beans for AWS SNS, SQS, and credentials provider.
 */
@Configuration
public class AwsIntegrationConfiguration {

    /**
     * AWS region to be used for services
     */
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    /**
     * SQS service endpoint URL
     */
    @Value("${aws.sqs.endpoint:}")
    private String sqsEndpointUrl;
    
    /**
     * SNS service endpoint URL
     */
    @Value("${aws.sns.endpoint:}")
    private String snsEndpointUrl;
    
    /**
     * AWS access key
     */
    @Value("${aws.credentials.access-key}")
    private String accessKey;
    
    /**
     * AWS secret key
     */
    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    /**
     * Creates and configures an Amazon SNS client.
     * 
     * @return Configured AmazonSNS client
     */
    @Bean
    public AmazonSNS amazonSNSClient() {
        AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider());
        
        // Configure endpoint if specified, otherwise use the default AWS endpoint for the region
        if (snsEndpointUrl != null && !snsEndpointUrl.isEmpty()) {
            // If endpoint URL is provided, use it with the specified region
            builder.withEndpointConfiguration(new EndpointConfiguration(snsEndpointUrl, awsRegion));
        } else {
            // Otherwise use standard region configuration
            builder.withRegion(awsRegion);
        }
        
        return builder.build();
    }

    /**
     * Creates and configures an Amazon SQS client.
     * 
     * @return Configured AmazonSQS client
     */
    @Bean
    public AmazonSQS amazonSQSClient() {
        AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider());
        
        // Configure endpoint if specified, otherwise use the default AWS endpoint for the region
        if (sqsEndpointUrl != null && !sqsEndpointUrl.isEmpty()) {
            // If endpoint URL is provided, use it with the specified region
            builder.withEndpointConfiguration(new EndpointConfiguration(sqsEndpointUrl, awsRegion));
        } else {
            // Otherwise use standard region configuration
            builder.withRegion(awsRegion);
        }
        
        return builder.build();
    }

    /**
     * Creates an AWS credentials provider using the configured access and secret keys.
     * 
     * @return AWSCredentialsProvider for authenticating with AWS services
     */
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        // Create static credentials provider with configured access and secret keys
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsCredentials);
        
        // TODO: Consider implementing a more robust credentials provider for production use
        // FIXME: Storing credentials in configuration is not secure for production environments
    }
}