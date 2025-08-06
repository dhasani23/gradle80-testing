package com.gradle80.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SnsPublisherImpl class.
 * <p>
 * Tests verify that the SNS publisher correctly interacts with the Amazon SNS client
 * and properly handles message serialization and attribute transformation.
 * </p>
 */
public class SnsPublisherTest {

    /**
     * Mock SNS client for testing
     */
    @Mock
    private AmazonSNS snsClient;

    /**
     * The publisher implementation under test
     */
    private SnsPublisherImpl snsPublisher;

    /**
     * Test topic ARN
     */
    private static final String TEST_TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:test-topic";
    
    /**
     * Test message ID returned by SNS
     */
    private static final String TEST_MESSAGE_ID = "test-message-id-12345";

    @Before
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);
        
        // Create a real ObjectMapper for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Create the publisher with mocked SNS client and real object mapper
        snsPublisher = new SnsPublisherImpl(snsClient, objectMapper);
        
        // Setup default mock behavior
        PublishResult mockResult = new PublishResult().withMessageId(TEST_MESSAGE_ID);
        when(snsClient.publish(any(PublishRequest.class))).thenReturn(mockResult);
    }

    /**
     * Test that the publishMessage method correctly serializes objects to JSON
     * and sends them to SNS with the appropriate topic ARN.
     */
    @Test
    public void testPublishMessage() {
        // Prepare test data
        TestMessage testMessage = new TestMessage("test-subject", "test-body");
        
        // Call the method under test
        PublishResult result = snsPublisher.publishMessage(TEST_TOPIC_ARN, testMessage);
        
        // Verify results
        assertNotNull("Result should not be null", result);
        assertEquals("Message ID should match", TEST_MESSAGE_ID, result.getMessageId());
        
        // Capture the request sent to SNS
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(snsClient).publish(requestCaptor.capture());
        
        // Verify the request properties
        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals("Topic ARN should match", TEST_TOPIC_ARN, capturedRequest.getTopicArn());
        assertTrue("Message should be JSON serialized and contain subject field", 
                capturedRequest.getMessage().contains("\"subject\""));
        assertTrue("Message should be JSON serialized and contain body field", 
                capturedRequest.getMessage().contains("\"body\""));
    }

    /**
     * Test that publishMessageWithAttributes correctly adds message attributes
     * to the SNS publish request and serializes the message content.
     */
    @Test
    public void testPublishMessageWithAttributes() {
        // Prepare test data
        TestMessage testMessage = new TestMessage("test-subject", "test-body");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("messageType", "notification");
        attributes.put("priority", "high");
        
        // Call the method under test
        PublishResult result = snsPublisher.publishMessageWithAttributes(
                TEST_TOPIC_ARN, testMessage, attributes);
        
        // Verify results
        assertNotNull("Result should not be null", result);
        assertEquals("Message ID should match", TEST_MESSAGE_ID, result.getMessageId());
        
        // Capture the request sent to SNS
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(snsClient).publish(requestCaptor.capture());
        
        // Verify the request properties
        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals("Topic ARN should match", TEST_TOPIC_ARN, capturedRequest.getTopicArn());
        
        // Verify message content
        assertTrue("Message should be JSON serialized and contain subject field", 
                capturedRequest.getMessage().contains("\"subject\""));
        assertTrue("Message should be JSON serialized and contain body field", 
                capturedRequest.getMessage().contains("\"body\""));
        
        // Verify message attributes
        Map<String, MessageAttributeValue> messageAttributes = capturedRequest.getMessageAttributes();
        assertNotNull("Message attributes should not be null", messageAttributes);
        assertEquals("Should have two attributes", 2, messageAttributes.size());
        
        // Check specific attributes
        MessageAttributeValue typeAttr = messageAttributes.get("messageType");
        assertNotNull("messageType attribute should exist", typeAttr);
        assertEquals("messageType data type should be String", "String", typeAttr.getDataType());
        assertEquals("messageType value should match", "notification", typeAttr.getStringValue());
        
        MessageAttributeValue priorityAttr = messageAttributes.get("priority");
        assertNotNull("priority attribute should exist", priorityAttr);
        assertEquals("priority data type should be String", "String", priorityAttr.getDataType());
        assertEquals("priority value should match", "high", priorityAttr.getStringValue());
    }

    /**
     * Test that exceptions from the SNS client are propagated correctly.
     */
    @Test(expected = RuntimeException.class)
    public void testPublishMessagePropagatesExceptions() {
        // Setup mock to throw exception
        when(snsClient.publish(any(PublishRequest.class)))
            .thenThrow(new RuntimeException("SNS service error"));
        
        // This should throw the exception
        snsPublisher.publishMessage(TEST_TOPIC_ARN, "test message");
        
        // The test will pass if the exception is thrown as expected
    }

    /**
     * Test that null attributes are handled gracefully and don't cause failures.
     */
    @Test
    public void testPublishMessageWithNullAttributes() {
        // Call with null attributes map
        PublishResult result = snsPublisher.publishMessageWithAttributes(
                TEST_TOPIC_ARN, "test message", null);
        
        // Verify results
        assertNotNull("Result should not be null", result);
        
        // Verify no attributes were added
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(snsClient).publish(requestCaptor.capture());
        PublishRequest capturedRequest = requestCaptor.getValue();
        
        // The map could either be null or empty depending on implementation
        assertTrue("Attributes map should be null or empty",
                capturedRequest.getMessageAttributes() == null || capturedRequest.getMessageAttributes().isEmpty());
    }
    
    /**
     * Simple test message class for serialization testing.
     */
    private static class TestMessage {
        private String subject;
        private String body;
        
        public TestMessage(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getBody() {
            return body;
        }
    }
    
    // TODO: Add tests for edge cases like very large messages or special characters
    
    // FIXME: Consider testing serialization failures once error handling strategy is finalized
}