package com.gradle80.aws.health;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for AwsHealthCheck
 */
public class AwsHealthCheckTest {

    @Mock
    private AmazonSQS sqsClient;

    @Mock
    private AmazonSNS snsClient;

    private AwsHealthCheck healthCheck;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        healthCheck = new AwsHealthCheck(sqsClient, snsClient);
    }

    @Test
    public void testCheckSqsHealth_Success() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());

        // Act
        boolean result = healthCheck.checkSqsHealth();

        // Assert
        assertTrue("SQS health check should return true when successful", result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    public void testCheckSqsHealth_Failure() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class)))
            .thenThrow(new AmazonClientException("SQS connection failed"));

        // Act
        boolean result = healthCheck.checkSqsHealth();

        // Assert
        assertFalse("SQS health check should return false when AWS client exception occurs", result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    public void testCheckSnsHealth_Success() {
        // Arrange
        when(snsClient.listTopics(any(ListTopicsRequest.class))).thenReturn(new ListTopicsResult());

        // Act
        boolean result = healthCheck.checkSnsHealth();

        // Assert
        assertTrue("SNS health check should return true when successful", result);
        verify(snsClient).listTopics(any(ListTopicsRequest.class));
    }

    @Test
    public void testCheckSnsHealth_Failure() {
        // Arrange
        when(snsClient.listTopics(any(ListTopicsRequest.class)))
            .thenThrow(new AmazonClientException("SNS connection failed"));

        // Act
        boolean result = healthCheck.checkSnsHealth();

        // Assert
        assertFalse("SNS health check should return false when AWS client exception occurs", result);
        verify(snsClient).listTopics(any(ListTopicsRequest.class));
    }

    @Test
    public void testGetHealthStatus_WhenBothServicesHealthy() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());
        when(snsClient.listTopics(any(ListTopicsRequest.class))).thenReturn(new ListTopicsResult());

        // Act
        HealthStatus status = healthCheck.getHealthStatus();

        // Assert
        assertTrue("SQS should be reported as healthy", status.isSqsHealthy());
        assertTrue("SNS should be reported as healthy", status.isSnsHealthy());
        assertTrue("Overall status should be healthy", status.isOverallHealthy());
        assertNotNull("Last checked timestamp should not be null", status.getLastChecked());
    }

    @Test
    public void testGetHealthStatus_WhenOneServiceUnhealthy() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());
        when(snsClient.listTopics(any(ListTopicsRequest.class)))
            .thenThrow(new AmazonClientException("SNS connection failed"));

        // Act
        HealthStatus status = healthCheck.getHealthStatus();

        // Assert
        assertTrue("SQS should be reported as healthy", status.isSqsHealthy());
        assertFalse("SNS should be reported as unhealthy", status.isSnsHealthy());
        assertFalse("Overall status should be unhealthy", status.isOverallHealthy());
    }

    @Test
    public void testGetHealthStatus_UsesCachedResults() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());
        when(snsClient.listTopics(any(ListTopicsRequest.class))).thenReturn(new ListTopicsResult());

        // Act - First call should perform actual checks
        HealthStatus firstStatus = healthCheck.getHealthStatus();
        
        // Clear invocations to verify no more calls in second request
        clearInvocations(sqsClient, snsClient);
        
        // Act - Second call should use cached results
        HealthStatus secondStatus = healthCheck.getHealthStatus();

        // Assert
        assertEquals("Second status should equal first status", firstStatus, secondStatus);
        verifyNoMoreInteractions(sqsClient, snsClient);
    }

    @Test
    public void testRefreshHealthStatus_ForcesFreshCheck() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());
        when(snsClient.listTopics(any(ListTopicsRequest.class))).thenReturn(new ListTopicsResult());
        
        // First call to cache status
        healthCheck.getHealthStatus();
        
        // Clear invocations
        clearInvocations(sqsClient, snsClient);

        // Act - Should force a fresh check
        healthCheck.refreshHealthStatus();

        // Assert
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
        verify(snsClient).listTopics(any(ListTopicsRequest.class));
    }
}