package com.gradle80.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS configuration for SNS client.
 */
@Configuration
public class AwsConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    /**
     * Creates the AWS credentials provider.
     * Uses the default AWS credentials provider chain which looks for credentials in multiple places:
     * - Environment variables
     * - Java system properties
     * - AWS credentials file (~/.aws/credentials)
     * - EC2 instance profile credentials
     *
     * @return the AWS credentials provider
     */
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    /**
     * Creates the Amazon SNS client.
     *
     * @param credentialsProvider the AWS credentials provider
     * @return the Amazon SNS client
     */
    @Bean
    public AmazonSNS amazonSNS(AWSCredentialsProvider credentialsProvider) {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.fromName(awsRegion))
                .build();
    }
}