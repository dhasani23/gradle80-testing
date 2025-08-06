package com.gradle80.aws.sqs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.gradle80.aws.sqs.model.SqsMessage;

/**
 * Unit tests for the RetryableMessageProcessor class.
 */
public class RetryableMessageProcessorTest {

    private static final String DLQ_URL = "https://sqs.us-east-1.amazonaws.com/123456789/test-dlq";
    private static final int MAX_RETRIES = 3;
    private static final String MESSAGE_ID = "test-message-id";
    private static final String RECEIPT_HANDLE = "test-receipt-handle";
    private static final String MESSAGE_BODY = "{\"key\":\"value\"}";

    @Mock
    private AmazonSQS sqsClient;

    @Mock
    private MessageProcessor delegateProcessor;

    private RetryableMessageProcessor processor;

    private SqsMessage testMessage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        processor = new RetryableMessageProcessor(sqsClient, DLQ_URL, MAX_RETRIES, delegateProcessor);
        
        // Create a test message
        testMessage = new SqsMessage(MESSAGE_ID, RECEIPT_HANDLE, MESSAGE_BODY, Collections.emptyMap());
    }

    @Test
    public void testSuccessfulMessageProcessing() {
        // Test successful processing (no exception)
        processor.processMessage(testMessage);

        // Verify delegate was called
        verify(delegateProcessor, times(1)).processMessage(testMessage);
        
        // Verify no DLQ interaction
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    public void testFailedMessageProcessingWithinRetryLimit() {
        // Setup delegate to throw exception
        doThrow(new RuntimeException("Test processing error"))
            .when(delegateProcessor).processMessage(testMessage);
        
        // Create a message with retry count = 1
        Map<String, String> attributes = new HashMap<>();
        attributes.put("RetryCount", "1");
        SqsMessage messageWithRetry = new SqsMessage(MESSAGE_ID, RECEIPT_HANDLE, MESSAGE_BODY, attributes);

        // Process message
        processor.processMessage(messageWithRetry);

        // Verify delegate was called
        verify(delegateProcessor, times(1)).processMessage(messageWithRetry);
        
        // Verify no DLQ message since we're still under max retries
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    public void testFailedMessageProcessingExceedingRetryLimit() {
        // Setup delegate to throw exception
        RuntimeException testException = new RuntimeException("Test processing error");
        doThrow(testException)
            .when(delegateProcessor).processMessage(any(SqsMessage.class));
        
        // Create a message with max retry count reached
        Map<String, String> attributes = new HashMap<>();
        attributes.put("RetryCount", String.valueOf(MAX_RETRIES));
        SqsMessage messageMaxRetries = new SqsMessage(MESSAGE_ID, RECEIPT_HANDLE, MESSAGE_BODY, attributes);

        // Process message
        processor.processMessage(messageMaxRetries);

        // Verify delegate was called
        verify(delegateProcessor, times(1)).processMessage(messageMaxRetries);
        
        // Verify message sent to DLQ
        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient, times(1)).sendMessage(requestCaptor.capture());
        
        SendMessageRequest capturedRequest = requestCaptor.getValue();
        assert(capturedRequest.getQueueUrl().equals(DLQ_URL));
        assert(capturedRequest.getMessageBody().equals(MESSAGE_BODY));
    }

    @Test
    public void testSendToDeadLetter() {
        Exception testException = new RuntimeException("Test exception");
        
        // Directly test the sendToDeadLetter method
        processor.sendToDeadLetter(testMessage, testException);
        
        // Verify message sent to DLQ
        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient, times(1)).sendMessage(requestCaptor.capture());
        
        SendMessageRequest capturedRequest = requestCaptor.getValue();
        assert(capturedRequest.getQueueUrl().equals(DLQ_URL));
        assert(capturedRequest.getMessageBody().equals(MESSAGE_BODY));
        
        // Verify error details included
        assert(capturedRequest.getMessageAttributes().containsKey("ErrorMessage"));
        assert(capturedRequest.getMessageAttributes().containsKey("ErrorType"));
    }

    @Test
    public void testNullMessageHandling() {
        // Test null message handling
        processor.processMessage(null);
        
        // Verify delegate was not called
        verify(delegateProcessor, never()).processMessage(any());
        
        // Verify no DLQ interaction
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorValidationForNullSqsClient() {
        new RetryableMessageProcessor(null, DLQ_URL, MAX_RETRIES, delegateProcessor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorValidationForNullDLQUrl() {
        new RetryableMessageProcessor(sqsClient, null, MAX_RETRIES, delegateProcessor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorValidationForEmptyDLQUrl() {
        new RetryableMessageProcessor(sqsClient, "", MAX_RETRIES, delegateProcessor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorValidationForNegativeRetries() {
        new RetryableMessageProcessor(sqsClient, DLQ_URL, -1, delegateProcessor);
    }
}