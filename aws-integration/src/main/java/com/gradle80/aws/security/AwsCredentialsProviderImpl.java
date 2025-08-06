package com.gradle80.aws.security;

import java.util.Objects;

/**
 * Implementation of the AWS credentials provider interface.
 * 
 * This implementation maintains static credentials (access key and secret key)
 * and provides them when requested. It supports credential refresh operations
 * but since static credentials are used, refresh is essentially a no-op.
 *
 * This implementation is suitable for development environments or scenarios
 * where credentials are externally managed and injected into the application.
 *
 * @since 1.0
 */
public class AwsCredentialsProviderImpl implements AwsCredentialsProvider {

    private String accessKey;
    private String secretKey;
    
    // Cache of credentials to avoid creating new objects on each request
    private volatile AWSCredentials cachedCredentials;

    /**
     * Creates a new provider with the specified credentials.
     *
     * @param accessKey AWS access key
     * @param secretKey AWS secret key
     */
    public AwsCredentialsProviderImpl(String accessKey, String secretKey) {
        setCredentials(accessKey, secretKey);
    }

    /**
     * Default constructor for cases where credentials will be set later.
     * 
     * Note: getCredentials() will throw an exception if called before
     * valid credentials are set.
     */
    public AwsCredentialsProviderImpl() {
        // Default constructor
    }

    /**
     * Sets or updates the AWS credentials.
     *
     * @param accessKey AWS access key
     * @param secretKey AWS secret key
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public void setCredentials(String accessKey, String secretKey) {
        if (isNullOrEmpty(accessKey)) {
            throw new IllegalArgumentException("Access key cannot be null or empty");
        }
        if (isNullOrEmpty(secretKey)) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        
        // Invalidate the cached credentials
        this.cachedCredentials = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AWSCredentials getCredentials() {
        if (isNullOrEmpty(accessKey) || isNullOrEmpty(secretKey)) {
            throw new IllegalStateException("AWS credentials have not been set");
        }
        
        // Use cached credentials if available
        if (cachedCredentials == null) {
            synchronized (this) {
                if (cachedCredentials == null) {
                    cachedCredentials = new BasicAWSCredentials(accessKey, secretKey);
                }
            }
        }
        
        return cachedCredentials;
    }

    /**
     * {@inheritDoc}
     * 
     * For this implementation, refresh is essentially a no-op since we're using
     * static credentials. In a real-world scenario, this might check for updated
     * credentials from an external source.
     */
    @Override
    public void refresh() {
        // For static credentials, refresh is a no-op
        // FIXME: Consider implementing a mechanism to check if credentials have been updated externally
        
        // TODO: Add logging to indicate refresh attempt
    }
    
    /**
     * Utility method to check if a string is null or empty.
     *
     * @param value the string to check
     * @return true if the string is null or empty, false otherwise
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + 
               "accessKey=" + (accessKey != null ? accessKey.substring(0, Math.min(4, accessKey.length())) + "..." : "null") + 
               ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AwsCredentialsProviderImpl that = (AwsCredentialsProviderImpl) o;
        
        return Objects.equals(accessKey, that.accessKey) &&
               Objects.equals(secretKey, that.secretKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accessKey, secretKey);
    }
}