package com.gradle80.aws.security;

/**
 * Represents a set of AWS credentials used for authenticating requests to AWS services.
 * 
 * AWS credentials typically consist of an access key ID and a secret access key.
 * Some credentials might also include a session token for temporary credentials.
 * 
 * @since 1.0
 */
public interface AWSCredentials {
    
    /**
     * Returns the AWS access key ID for these credentials.
     * 
     * @return The AWS access key ID
     */
    String getAWSAccessKeyId();
    
    /**
     * Returns the AWS secret access key for these credentials.
     * 
     * @return The AWS secret access key
     */
    String getAWSSecretKey();
}