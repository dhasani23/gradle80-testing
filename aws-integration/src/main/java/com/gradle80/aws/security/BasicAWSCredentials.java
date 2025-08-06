package com.gradle80.aws.security;

/**
 * Basic implementation of the AWSCredentials interface that provides
 * immutable AWS credentials.
 * 
 * This implementation is suitable for credentials that don't change
 * during the lifetime of the application.
 * 
 * @since 1.0
 */
public class BasicAWSCredentials implements AWSCredentials {
    
    private final String accessKeyId;
    private final String secretKey;
    
    /**
     * Constructs a new BasicAWSCredentials with the specified access key and secret key.
     * 
     * @param accessKeyId The AWS access key ID
     * @param secretKey The AWS secret access key
     */
    public BasicAWSCredentials(String accessKeyId, String secretKey) {
        if (accessKeyId == null) {
            throw new IllegalArgumentException("Access key ID cannot be null");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key cannot be null");
        }
        
        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAWSAccessKeyId() {
        return accessKeyId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + 
               "accessKeyId=" + accessKeyId.substring(0, 4) + "..." + 
               ")";
    }
}