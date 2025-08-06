package com.gradle80.aws.integration;

import com.gradle80.aws.sns.SnsPublisher;
import com.gradle80.aws.sns.TopicManager;
import com.gradle80.aws.sqs.QueueManager;
import com.gradle80.aws.sqs.SqsListener;
import com.gradle80.aws.sqs.model.SqsMessage;
import com.gradle80.aws.sqs.model.MessageProcessor;
import com.gradle80.aws.sns.model.SnsMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Integration tests for AWS messaging services (SNS/SQS).
 * This test suite verifies the end-to-end functionality of publishing messages to SNS topics
 * and receiving them through subscribed SQS queues.
 * 
 * Requires running AWS infrastructure (can be LocalStack for local development).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("aws-test")
public class AwsIntegrationIT {

    @Autowired
    private SnsPublisher snsPublisher;

    @Autowired
    private SqsListener sqsListener;

    @Autowired
    private QueueManager queueManager;

    @Autowired
    private TopicManager topicManager;

    private String topicArn;
    private String queueUrl;
    private String subscriptionArn;
    
    // Test message contents
    private static final String TEST_MESSAGE = "Test message from integration test";
    private static final int WAIT_TIMEOUT_SECONDS = 30;

    /**
     * Set up test infrastructure - creates a topic and queue, and subscribes the queue to the topic.
     */
    @Before
    public void setUp() throws Exception {
        // Generate unique names for test resources
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String topicName = "test-topic-" + uniqueId;
        String queueName = "test-queue-" + uniqueId;
        
        // Create topic and queue
        topicArn = topicManager.createTopic(topicName);
        assertNotNull("Topic ARN should not be null", topicArn);
        
        queueUrl = queueManager.createQueue(queueName);
        assertNotNull("Queue URL should not be null", queueUrl);
        
        // Extract queue ARN from attributes
        Map<String, String> queueAttributes = queueManager.getQueueAttributes(queueUrl);
        String queueArn = queueAttributes.get("QueueArn");
        assertNotNull("Queue ARN should be available in attributes", queueArn);
        
        // Subscribe queue to topic
        subscriptionArn = topicManager.subscribeToTopic(topicArn, "sqs", queueArn);
        assertNotNull("Subscription ARN should not be null", subscriptionArn);
        
        // Allow time for subscription to be fully active
        Thread.sleep(2000);
        
        System.out.println("Test infrastructure created:");
        System.out.println("Topic ARN: " + topicArn);
        System.out.println("Queue URL: " + queueUrl);
        System.out.println("Subscription ARN: " + subscriptionArn);
    }

    /**
     * Tests the end-to-end flow of publishing a message to SNS and receiving it on SQS.
     * Uses a CountDownLatch to synchronize the test thread with the message processing thread.
     */
    @Test
    public void testEnd2EndMessagePublishingAndReceiving() throws Exception {
        // Set up synchronization mechanism to wait for message receipt
        final CountDownLatch messageReceived = new CountDownLatch(1);
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        
        // Create a message processor that will verify the received message
        MessageProcessor testProcessor = message -> {
            try {
                String messageBody = message.getBody();
                System.out.println("Received message: " + messageBody);
                
                // Verify the message contains our test content
                if (messageBody.contains(TEST_MESSAGE)) {
                    testPassed.set(true);
                }
            } finally {
                // Signal that we've processed a message
                messageReceived.countDown();
            }
        };
        
        // Register our message processor and start listening
        sqsListener.registerMessageProcessor(queueUrl, testProcessor);
        sqsListener.startListening(queueUrl);
        
        try {
            // Publish a test message
            Map<String, String> messageAttributes = new HashMap<>();
            messageAttributes.put("testAttribute", "testValue");
            messageAttributes.put("source", "integrationTest");
            
            System.out.println("Publishing message to topic: " + topicArn);
            snsPublisher.publishMessageWithAttributes(topicArn, TEST_MESSAGE, messageAttributes);
            
            // Wait for the message to be received
            boolean receivedInTime = messageReceived.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            // Verify test results
            assertTrue("Timed out waiting for message to be received", receivedInTime);
            assertTrue("Message content verification failed", testPassed.get());
        } finally {
            // Stop listening regardless of test outcome
            sqsListener.stopListening(queueUrl);
        }
    }

    /**
     * Clean up test infrastructure.
     */
    @After
    public void tearDown() throws Exception {
        // Clean up test resources to avoid cluttering AWS account
        try {
            // Unsubscribe queue from topic
            if (subscriptionArn != null) {
                System.out.println("Unsubscribing from topic: " + subscriptionArn);
                topicManager.unsubscribeFromTopic(subscriptionArn);
            }
            
            // Delete queue
            if (queueUrl != null) {
                System.out.println("Deleting queue: " + queueUrl);
                queueManager.deleteQueue(queueUrl);
            }
            
            // Delete topic
            if (topicArn != null) {
                System.out.println("Deleting topic: " + topicArn);
                topicManager.deleteTopic(topicArn);
            }
        } catch (Exception e) {
            System.err.println("Error during test cleanup: " + e.getMessage());
            e.printStackTrace();
            // Don't rethrow - we want other cleanup to proceed even if some steps fail
        }
    }
}