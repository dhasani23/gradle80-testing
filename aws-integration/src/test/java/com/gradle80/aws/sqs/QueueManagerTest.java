package com.gradle80.aws.sqs;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;

/**
 * Test class for {@link QueueManager} that verifies its functionality for
 * creating, deleting, and managing SQS queues.
 */
public class QueueManagerTest {

    /**
     * Mock SQS client for simulating AWS SQS interactions
     */
    @Mock
    private AmazonSQS sqsClient;
    
    /**
     * QueueManager instance under test
     */
    private QueueManager queueManager;
    
    // Test constants
    private static final String QUEUE_NAME = "test-queue";
    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    /**
     * Sets up the test environment before each test.
     * Initializes mocks and creates a fresh QueueManager instance.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        queueManager = new QueueManager(sqsClient);
        
        // Default mock setup for successful queue creation
        CreateQueueResult createQueueResult = new CreateQueueResult().withQueueUrl(QUEUE_URL);
        when(sqsClient.createQueue(any(CreateQueueRequest.class))).thenReturn(createQueueResult);
        
        // Default mock setup for queue attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put("ApproximateNumberOfMessages", "0");
        attributes.put("CreatedTimestamp", "1617234567890");
        GetQueueAttributesResult getQueueAttributesResult = new GetQueueAttributesResult().withAttributes(attributes);
        when(sqsClient.getQueueAttributes(any(GetQueueAttributesRequest.class))).thenReturn(getQueueAttributesResult);
    }
    
    /**
     * Tests the queue creation functionality.
     * Verifies that the QueueManager correctly calls the SQS client and returns the queue URL.
     */
    @Test
    public void testCreateQueue() {
        String resultUrl = queueManager.createQueue(QUEUE_NAME);
        
        // Verify the queue was created with the correct request
        verify(sqsClient).createQueue(any(CreateQueueRequest.class));
        
        // Verify the returned URL matches the expected one
        assertEquals("Queue URL should match the expected value", QUEUE_URL, resultUrl);
    }
    
    /**
     * Tests the queue creation with exception handling.
     * Verifies that the QueueManager properly propagates exceptions from the SQS client.
     */
    @Test
    public void testCreateQueueWithException() {
        // Setup mock to throw exception
        when(sqsClient.createQueue(any(CreateQueueRequest.class)))
            .thenThrow(new RuntimeException("Queue creation failed"));
        
        try {
            queueManager.createQueue(QUEUE_NAME);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            // Expected exception
            assertEquals("Queue creation failed", e.getMessage());
        }
    }
    
    /**
     * Tests the queue deletion functionality.
     * Verifies that the QueueManager correctly calls the SQS client to delete a queue.
     */
    @Test
    public void testDeleteQueue() {
        queueManager.deleteQueue(QUEUE_URL);
        
        // Verify the queue deletion was called with the correct URL
        verify(sqsClient).deleteQueue(QUEUE_URL);
    }
    
    /**
     * Tests queue deletion when the queue does not exist.
     * Verifies that the QueueManager properly propagates QueueDoesNotExistException.
     */
    @Test(expected = QueueDoesNotExistException.class)
    public void testDeleteNonExistentQueue() {
        // Setup mock to throw QueueDoesNotExistException
        doThrow(new QueueDoesNotExistException("Queue does not exist"))
            .when(sqsClient).deleteQueue(QUEUE_URL);
        
        queueManager.deleteQueue(QUEUE_URL);
        
        // Should throw QueueDoesNotExistException
    }
    
    /**
     * Tests retrieval of queue attributes.
     * Verifies that the QueueManager correctly retrieves and converts queue attributes.
     */
    @Test
    public void testGetQueueAttributes() {
        Map<String, String> resultAttributes = queueManager.getQueueAttributes(QUEUE_URL);
        
        // Verify getQueueAttributes was called with the correct request
        verify(sqsClient).getQueueAttributes(argThat(request -> 
            request.getQueueUrl().equals(QUEUE_URL) && 
            request.getAttributeNames().contains("All")
        ));
        
        // Verify the returned attributes match the expected ones
        assertNotNull("Attributes should not be null", resultAttributes);
        assertEquals("Should have 2 attributes", 2, resultAttributes.size());
        assertEquals("ApproximateNumberOfMessages should be 0", "0", resultAttributes.get("ApproximateNumberOfMessages"));
        assertEquals("CreatedTimestamp should match", "1617234567890", resultAttributes.get("CreatedTimestamp"));
    }
    
    /**
     * Tests retrieval of queue attributes with empty result.
     * Verifies that the QueueManager handles empty attribute maps correctly.
     */
    @Test
    public void testGetQueueAttributesEmpty() {
        // Setup mock for empty attributes
        GetQueueAttributesResult emptyResult = new GetQueueAttributesResult().withAttributes(new HashMap<>());
        when(sqsClient.getQueueAttributes(any(GetQueueAttributesRequest.class))).thenReturn(emptyResult);
        
        Map<String, String> resultAttributes = queueManager.getQueueAttributes(QUEUE_URL);
        
        // Verify the returned attributes are empty but not null
        assertNotNull("Attributes should not be null", resultAttributes);
        assertTrue("Attributes should be empty", resultAttributes.isEmpty());
    }
    
    /**
     * Tests retrieving queue attributes with exception handling.
     * Verifies that the QueueManager properly propagates exceptions from the SQS client.
     */
    @Test
    public void testGetQueueAttributesWithException() {
        // Setup mock to throw exception
        when(sqsClient.getQueueAttributes(any(GetQueueAttributesRequest.class)))
            .thenThrow(new RuntimeException("Failed to get attributes"));
        
        try {
            queueManager.getQueueAttributes(QUEUE_URL);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            // Expected exception
            assertEquals("Failed to get attributes", e.getMessage());
        }
    }
    
    /**
     * Tests purging a queue.
     * Verifies that the QueueManager correctly calls the SQS client to purge a queue.
     * 
     * Note: This test is included to test functionality that exists in QueueManager
     * but wasn't explicitly requested in the test class blueprint.
     */
    @Test
    public void testPurgeQueue() {
        queueManager.purgeQueue(QUEUE_URL);
        
        // Verify purgeQueue was called with the correct URL
        verify(sqsClient).purgeQueue(argThat(request -> 
            request.getQueueUrl().equals(QUEUE_URL)
        ));
    }
    
    // TODO: Add tests for queue creation with attributes when that functionality is implemented
    
    // TODO: Consider adding integration tests that test against a real SQS service or a local emulator
}