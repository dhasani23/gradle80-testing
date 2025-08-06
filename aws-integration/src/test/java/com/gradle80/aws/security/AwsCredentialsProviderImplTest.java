package com.gradle80.aws.security;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * Unit tests for the AwsCredentialsProviderImpl class.
 */
public class AwsCredentialsProviderImplTest {

    private static final String TEST_ACCESS_KEY = "AKIATESTKEY123456789";
    private static final String TEST_SECRET_KEY = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEF";
    
    private AwsCredentialsProviderImpl provider;
    
    @Before
    public void setUp() {
        provider = new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, TEST_SECRET_KEY);
    }
    
    @Test
    public void testGetCredentialsReturnsValidCredentials() {
        // When
        AWSCredentials credentials = provider.getCredentials();
        
        // Then
        assertNotNull("Credentials should not be null", credentials);
        assertEquals("Access key should match", TEST_ACCESS_KEY, credentials.getAWSAccessKeyId());
        assertEquals("Secret key should match", TEST_SECRET_KEY, credentials.getAWSSecretKey());
    }
    
    @Test
    public void testCredentialCaching() {
        // When
        AWSCredentials credentials1 = provider.getCredentials();
        AWSCredentials credentials2 = provider.getCredentials();
        
        // Then
        assertSame("Credentials should be cached and return the same instance", credentials1, credentials2);
    }
    
    @Test
    public void testRefreshDoesNotThrowException() {
        // Should not throw an exception
        provider.refresh();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullAccessKeyThrowsException() {
        new AwsCredentialsProviderImpl(null, TEST_SECRET_KEY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullSecretKeyThrowsException() {
        new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyAccessKeyThrowsException() {
        new AwsCredentialsProviderImpl("", TEST_SECRET_KEY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptySecretKeyThrowsException() {
        new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, "");
    }
    
    @Test
    public void testDefaultConstructorAndSetCredentials() {
        // Given
        AwsCredentialsProviderImpl emptyProvider = new AwsCredentialsProviderImpl();
        
        // When
        emptyProvider.setCredentials(TEST_ACCESS_KEY, TEST_SECRET_KEY);
        AWSCredentials credentials = emptyProvider.getCredentials();
        
        // Then
        assertNotNull("Credentials should not be null", credentials);
        assertEquals("Access key should match", TEST_ACCESS_KEY, credentials.getAWSAccessKeyId());
        assertEquals("Secret key should match", TEST_SECRET_KEY, credentials.getAWSSecretKey());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testGetCredentialsWithoutSettingThrowsException() {
        // Given
        AwsCredentialsProviderImpl emptyProvider = new AwsCredentialsProviderImpl();
        
        // When
        emptyProvider.getCredentials();
        
        // Then: exception
    }
    
    @Test
    public void testEquality() {
        // Given
        AwsCredentialsProviderImpl provider1 = new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, TEST_SECRET_KEY);
        AwsCredentialsProviderImpl provider2 = new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, TEST_SECRET_KEY);
        AwsCredentialsProviderImpl provider3 = new AwsCredentialsProviderImpl("different", TEST_SECRET_KEY);
        
        // Then
        assertEquals("Equal providers should be equal", provider1, provider2);
        assertNotEquals("Different providers should not be equal", provider1, provider3);
    }
    
    @Test
    public void testHashCodeConsistency() {
        // Given
        AwsCredentialsProviderImpl provider1 = new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, TEST_SECRET_KEY);
        AwsCredentialsProviderImpl provider2 = new AwsCredentialsProviderImpl(TEST_ACCESS_KEY, TEST_SECRET_KEY);
        
        // Then
        assertEquals("Equal providers should have same hashCode", provider1.hashCode(), provider2.hashCode());
    }
    
    @Test
    public void testToStringNotExposingSecrets() {
        // When
        String stringRepresentation = provider.toString();
        
        // Then
        assertTrue("toString() should include class name", stringRepresentation.contains("AwsCredentialsProviderImpl"));
        assertTrue("toString() should include partial access key", stringRepresentation.contains("AKIA"));
        assertFalse("toString() should not include the full access key", stringRepresentation.contains(TEST_ACCESS_KEY));
        assertFalse("toString() should not include the secret key", stringRepresentation.contains(TEST_SECRET_KEY));
    }
}