package com.gradle80.service.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SnsClient}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SnsClientTest {

    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:test-topic";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_MESSAGE = "Test message content";
    private static final String MESSAGE_ID = "message-id-123456";

    @Mock
    private AmazonSNS mockSnsClient;

    private SnsClient snsClient;

    @Before
    public void setUp() {
        // Create the SnsClient with the mock AWS client
        snsClient = new SnsClient(mockSnsClient, TOPIC_ARN);
        
        // Set up the mock to return a predictable result
        PublishResult mockResult = new PublishResult().withMessageId(MESSAGE_ID);
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(mockResult);
    }

    @Test
    public void testPublishMessage() {
        // Call the method under test
        String result = snsClient.publishMessage(TEST_SUBJECT, TEST_MESSAGE);
        
        // Verify the result
        assertEquals("The message ID should match the expected value", MESSAGE_ID, result);
        
        // Capture the request to verify its properties
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(1)).publish(requestCaptor.capture());
        
        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals("The topic ARN should match", TOPIC_ARN, capturedRequest.getTopicArn());
        assertEquals("The subject should match", TEST_SUBJECT, capturedRequest.getSubject());
        assertEquals("The message should match", TEST_MESSAGE, capturedRequest.getMessage());
    }
    
    @Test(expected = RuntimeException.class)
    public void testPublishMessageWithException() {
        // Set up the mock to throw an exception
        when(mockSnsClient.publish(any(PublishRequest.class))).thenThrow(new RuntimeException("Test exception"));
        
        // Call the method under test - should throw RuntimeException
        snsClient.publishMessage(TEST_SUBJECT, TEST_MESSAGE);
    }
    
    @Test
    public void testGetTopicArn() {
        // Call the method under test
        String result = snsClient.getTopicArn();
        
        // Verify the result
        assertEquals("The topic ARN should match the expected value", TOPIC_ARN, result);
    }
}