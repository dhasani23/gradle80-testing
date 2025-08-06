package com.gradle80.aws.security;

/**
 * Interface for providing AWS credentials.
 * 
 * This interface defines the contract for retrieving and refreshing AWS credentials,
 * which are necessary for authenticating requests to AWS services.
 * 
 * Implementations of this interface should handle credential management strategies such as:
 * - Static credentials
 * - Environment variables
 * - EC2 Instance profiles
 * - Credential files
 * - Token-based authentication
 * 
 * @since 1.0
 */
public interface AwsCredentialsProvider {
    
    /**
     * Retrieves AWS credentials.
     * 
     * This method should return valid AWS credentials that can be used
     * to sign requests to AWS services. The implementation should handle
     * credential resolution according to the specific strategy it implements.
     * 
     * @return AWSCredentials object containing access key and secret key
     * @throws SecurityException if credentials cannot be retrieved
     */
    AWSCredentials getCredentials();
    
    /**
     * Refreshes the cached AWS credentials if applicable.
     * 
     * This method should update any cached credentials that the provider
     * might be using. For providers that don't cache credentials, this
     * method may have no effect.
     * 
     * It should be called when credentials might have been changed externally
     * or expired.
     */
    void refresh();
}