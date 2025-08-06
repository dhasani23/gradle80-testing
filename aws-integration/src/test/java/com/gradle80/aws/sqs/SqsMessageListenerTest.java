package com.gradle80.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.gradle80.aws.sqs.model.SqsMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SQS message listener
 */
public class SqsMessageListenerTest {
    
    /**
     * Mock SQS client for testing
     */
    private AmazonSQS sqsClient;
    
    /**
     * Mock message processor for testing
     */
    private MessageProcessor messageProcessor;
    
    /**
     * SQS listener under test
     */
    private SqsMessageListener messageListener;
    
    private static final String TEST_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    private static final String TEST_MESSAGE_ID = "test-message-id";
    private static final String TEST_RECEIPT_HANDLE = "test-receipt-handle";
    private static final String TEST_MESSAGE_BODY = "{\"data\":\"test message content\"}";
    
    /**
     * Setup method executed before each test.
     * Initializes mocks and the SQS message listener.
     */
    @Before
    public void setUp() {
        // Initialize mocks
        sqsClient = Mockito.mock(AmazonSQS.class);
        messageProcessor = Mockito.mock(MessageProcessor.class);
        
        // Create a Map of message processors with our test queue URL and processor
        Map<String, MessageProcessor> processors = new HashMap<>();
        processors.put(TEST_QUEUE_URL, messageProcessor);
        
        // Initialize the message listener with mocks and a short polling interval
        messageListener = new SqsMessageListener(sqsClient, processors, 100);
    }
    
    /**
     * Tests that the listener correctly receives and processes messages.
     * Verifies:
     * 1. Messages are received from SQS with the correct parameters
     * 2. Messages are processed by the message processor
     * 3. Successfully processed messages are deleted from the queue
     */
    @Test
    public void testReceiveAndProcessMessages() {
        // Set up test SQS messages
        Message message1 = new Message()
                .withMessageId(TEST_MESSAGE_ID)
                .withReceiptHandle(TEST_RECEIPT_HANDLE)
                .withBody(TEST_MESSAGE_BODY)
                .withAttributes(Collections.singletonMap("SenderId", "AIDAEXAMPLE"));
                
        // Configure mock SQS client to return our test message
        ReceiveMessageResult result = new ReceiveMessageResult()
                .withMessages(Collections.singletonList(message1));
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(result);
        
        // Execute the method under test
        messageListener.receiveAndProcessMessages(TEST_QUEUE_URL);
        
        // Capture the receive request to verify its properties
        ArgumentCaptor<ReceiveMessageRequest> receiveRequestCaptor = 
            ArgumentCaptor.forClass(ReceiveMessageRequest.class);
        verify(sqsClient).receiveMessage(receiveRequestCaptor.capture());
        
        // Verify the receive request was correctly constructed
        ReceiveMessageRequest capturedRequest = receiveRequestCaptor.getValue();
        assertEquals(TEST_QUEUE_URL, capturedRequest.getQueueUrl());
        assertEquals(Integer.valueOf(5), capturedRequest.getWaitTimeSeconds());
        
        // Verify the message processor was called with the correct SqsMessage
        ArgumentCaptor<SqsMessage> messageCaptor = ArgumentCaptor.forClass(SqsMessage.class);
        verify(messageProcessor).processMessage(messageCaptor.capture());
        
        SqsMessage capturedMessage = messageCaptor.getValue();
        assertEquals(TEST_MESSAGE_ID, capturedMessage.getMessageId());
        assertEquals(TEST_RECEIPT_HANDLE, capturedMessage.getReceiptHandle());
        assertEquals(TEST_MESSAGE_BODY, capturedMessage.getBody());
        
        // Verify the message was deleted after processing
        ArgumentCaptor<DeleteMessageRequest> deleteRequestCaptor = 
            ArgumentCaptor.forClass(DeleteMessageRequest.class);
        verify(sqsClient).deleteMessage(deleteRequestCaptor.capture());
        
        DeleteMessageRequest capturedDeleteRequest = deleteRequestCaptor.getValue();
        assertEquals(TEST_QUEUE_URL, capturedDeleteRequest.getQueueUrl());
        assertEquals(TEST_RECEIPT_HANDLE, capturedDeleteRequest.getReceiptHandle());
    }
    
    /**
     * Tests the handling of multiple messages in a batch.
     */
    @Test
    public void testReceiveAndProcessMultipleMessages() {
        // Set up multiple test messages
        Message message1 = new Message()
                .withMessageId("message-id-1")
                .withReceiptHandle("receipt-handle-1")
                .withBody("{\"id\":1}");
                
        Message message2 = new Message()
                .withMessageId("message-id-2")
                .withReceiptHandle("receipt-handle-2")
                .withBody("{\"id\":2}");
        
        // Configure mock to return multiple messages
        ReceiveMessageResult result = new ReceiveMessageResult()
                .withMessages(Arrays.asList(message1, message2));
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(result);
        
        // Execute the method under test
        messageListener.receiveAndProcessMessages(TEST_QUEUE_URL);
        
        // Verify processor was called twice (once for each message)
        verify(messageProcessor, times(2)).processMessage(any(SqsMessage.class));
        
        // Verify both messages were deleted
        verify(sqsClient, times(2)).deleteMessage(any(DeleteMessageRequest.class));
    }
    
    /**
     * Tests that exceptions during message processing are handled properly.
     * The listener should:
     * 1. Continue processing other messages
     * 2. Not delete the failed message (allowing it to become visible again)
     */
    @Test
    public void testProcessingExceptionHandling() {
        // Set up multiple test messages
        Message message1 = new Message()
                .withMessageId("message-id-1")
                .withReceiptHandle("receipt-handle-1")
                .withBody("{\"id\":1}");
                
        Message message2 = new Message()
                .withMessageId("message-id-2")
                .withReceiptHandle("receipt-handle-2")
                .withBody("{\"id\":2}");
        
        ReceiveMessageResult result = new ReceiveMessageResult()
                .withMessages(Arrays.asList(message1, message2));
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(result);
        
        // Make the first message throw an exception during processing
        doThrow(new RuntimeException("Processing error"))
            .when(messageProcessor).processMessage(argThat(msg -> 
                "message-id-1".equals(msg.getMessageId())));
        
        // Execute the method under test
        messageListener.receiveAndProcessMessages(TEST_QUEUE_URL);
        
        // Verify processor was called for both messages
        verify(messageProcessor, times(2)).processMessage(any(SqsMessage.class));
        
        // Verify only the second message was deleted (the first failed)
        ArgumentCaptor<DeleteMessageRequest> deleteRequestCaptor = 
            ArgumentCaptor.forClass(DeleteMessageRequest.class);
        verify(sqsClient, times(1)).deleteMessage(deleteRequestCaptor.capture());
        
        assertEquals("receipt-handle-2", deleteRequestCaptor.getValue().getReceiptHandle());
    }
    
    /**
     * Tests the start and stop lifecycle of the SQS message listener.
     * Verifies:
     * 1. Listener can be started for a queue
     * 2. isListening() reports correct status
     * 3. Listener can be stopped
     * 
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void testStartAndStopListening() throws InterruptedException {
        // Configure mock to return empty results (no messages)
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
            .thenReturn(new ReceiveMessageResult());
        
        // Verify listener is not active before starting
        assertFalse(messageListener.isListening(TEST_QUEUE_URL));
        
        // Start the listener
        messageListener.startListening(TEST_QUEUE_URL);
        
        // Verify listener is active
        assertTrue(messageListener.isListening(TEST_QUEUE_URL));
        
        // Allow some time for the listener thread to execute
        TimeUnit.MILLISECONDS.sleep(500);
        
        // Stop the listener
        messageListener.stopListening(TEST_QUEUE_URL);
        
        // Verify listener is no longer active
        assertFalse(messageListener.isListening(TEST_QUEUE_URL));
        
        // Verify receive message was called at least once
        // (exact number depends on timing so we can't assert an exact count)
        verify(sqsClient, atLeastOnce()).receiveMessage(any(ReceiveMessageRequest.class));
    }
    
    /**
     * Tests that appropriate exceptions are thrown when attempting to start a
     * listener without registering a message processor first.
     */
    @Test(expected = IllegalStateException.class)
    public void testStartListeningWithNoProcessor() {
        String queueWithNoProcessor = "https://sqs.us-east-1.amazonaws.com/123456789012/no-processor-queue";
        messageListener.startListening(queueWithNoProcessor);
        // Expected to throw IllegalStateException
    }
    
    /**
     * Tests that appropriate exceptions are thrown when attempting to start
     * a listener that's already running.
     */
    @Test(expected = IllegalStateException.class)
    public void testStartAlreadyRunningListener() {
        // Configure mock to return empty results
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
            .thenReturn(new ReceiveMessageResult());
            
        // Start listener
        messageListener.startListening(TEST_QUEUE_URL);
        
        try {
            // Attempt to start again - should throw exception
            messageListener.startListening(TEST_QUEUE_URL);
        } finally {
            // Clean up
            messageListener.stopListening(TEST_QUEUE_URL);
        }
    }
    
    /**
     * Tests that the stopAllListeners method correctly shuts down all active listeners.
     * 
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    public void testStopAllListeners() throws InterruptedException {
        // Configure mock to return empty results
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
            .thenReturn(new ReceiveMessageResult());
            
        // Register a processor for a second queue and start both listeners
        String secondQueueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/second-queue";
        messageListener.registerProcessor(secondQueueUrl, messageProcessor);
        
        messageListener.startListening(TEST_QUEUE_URL);
        messageListener.startListening(secondQueueUrl);
        
        // Verify both listeners are active
        assertTrue(messageListener.isListening(TEST_QUEUE_URL));
        assertTrue(messageListener.isListening(secondQueueUrl));
        
        // Allow some time for the listener threads to execute
        TimeUnit.MILLISECONDS.sleep(300);
        
        // Stop all listeners
        messageListener.stopAllListeners();
        
        // Verify both listeners are stopped
        assertFalse(messageListener.isListening(TEST_QUEUE_URL));
        assertFalse(messageListener.isListening(secondQueueUrl));
    }
    
    // TODO: Add tests for polling interval configuration
    // FIXME: Consider adding tests for message batch processing once implemented
}