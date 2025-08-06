package com.gradle80.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TopicManager class.
 * 
 * This class contains tests for creating topics, deleting topics, and subscribing to topics using the
 * TopicManager service with a mocked AWS SNS client.
 */
public class TopicManagerTest {

    @Mock
    private AmazonSNS snsClient;

    private TopicManager topicManager;

    private static final String TEST_TOPIC_NAME = "test-topic";
    private static final String TEST_TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:test-topic";
    private static final String TEST_SUBSCRIPTION_ARN = "arn:aws:sns:us-east-1:123456789012:test-topic:subscription123";
    private static final String TEST_PROTOCOL = "email";
    private static final String TEST_ENDPOINT = "test@example.com";

    /**
     * Set up test fixtures before each test method.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        topicManager = new TopicManager(snsClient);
    }

    /**
     * Test for creating an SNS topic successfully.
     */
    @Test
    public void testCreateTopic() {
        // Arrange
        CreateTopicResult mockResult = new CreateTopicResult().withTopicArn(TEST_TOPIC_ARN);
        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenReturn(mockResult);

        // Act
        String resultTopicArn = topicManager.createTopic(TEST_TOPIC_NAME);

        // Assert
        assertEquals("The returned topic ARN should match the expected value", TEST_TOPIC_ARN, resultTopicArn);
        verify(snsClient).createTopic(any(CreateTopicRequest.class));
    }

    /**
     * Test for creating a topic with invalid parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTopicWithNullName() {
        // Act
        topicManager.createTopic(null);
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for creating a topic with empty name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTopicWithEmptyName() {
        // Act
        topicManager.createTopic("");
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for handling exceptions during topic creation.
     */
    @Test(expected = RuntimeException.class)
    public void testCreateTopicWithSnsException() {
        // Arrange
        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenThrow(new AmazonSNSException("Test exception"));

        // Act
        topicManager.createTopic(TEST_TOPIC_NAME);
        // Assert: expect RuntimeException
    }

    /**
     * Test for deleting an SNS topic successfully.
     */
    @Test
    public void testDeleteTopic() {
        // Arrange
        doNothing().when(snsClient).deleteTopic(any(DeleteTopicRequest.class));

        // Act
        topicManager.deleteTopic(TEST_TOPIC_ARN);

        // Assert
        verify(snsClient).deleteTopic(any(DeleteTopicRequest.class));
    }

    /**
     * Test for deleting a topic with null ARN.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTopicWithNullArn() {
        // Act
        topicManager.deleteTopic(null);
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for handling exceptions during topic deletion.
     */
    @Test(expected = RuntimeException.class)
    public void testDeleteTopicWithSnsException() {
        // Arrange
        doThrow(new AmazonSNSException("Test exception")).when(snsClient).deleteTopic(any(DeleteTopicRequest.class));

        // Act
        topicManager.deleteTopic(TEST_TOPIC_ARN);
        // Assert: expect RuntimeException
    }

    /**
     * Test for subscribing to an SNS topic successfully.
     */
    @Test
    public void testSubscribeToTopic() {
        // Arrange
        SubscribeResult mockResult = new SubscribeResult().withSubscriptionArn(TEST_SUBSCRIPTION_ARN);
        when(snsClient.subscribe(any(SubscribeRequest.class))).thenReturn(mockResult);

        // Act
        String resultSubscriptionArn = topicManager.subscribeToTopic(TEST_TOPIC_ARN, TEST_PROTOCOL, TEST_ENDPOINT);

        // Assert
        assertEquals("The returned subscription ARN should match the expected value", 
                     TEST_SUBSCRIPTION_ARN, resultSubscriptionArn);
        
        // Verify the subscribe request was made with correct parameters
        verify(snsClient).subscribe(argThat(request -> 
            request.getTopicArn().equals(TEST_TOPIC_ARN) && 
            request.getProtocol().equals(TEST_PROTOCOL) && 
            request.getEndpoint().equals(TEST_ENDPOINT)));
    }

    /**
     * Test for subscribing with null topic ARN.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeToTopicWithNullArn() {
        // Act
        topicManager.subscribeToTopic(null, TEST_PROTOCOL, TEST_ENDPOINT);
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for subscribing with null protocol.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeToTopicWithNullProtocol() {
        // Act
        topicManager.subscribeToTopic(TEST_TOPIC_ARN, null, TEST_ENDPOINT);
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for subscribing with null endpoint.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeToTopicWithNullEndpoint() {
        // Act
        topicManager.subscribeToTopic(TEST_TOPIC_ARN, TEST_PROTOCOL, null);
        // Assert: expect IllegalArgumentException
    }

    /**
     * Test for handling exceptions during subscription.
     */
    @Test(expected = RuntimeException.class)
    public void testSubscribeToTopicWithSnsException() {
        // Arrange
        when(snsClient.subscribe(any(SubscribeRequest.class))).thenThrow(new AmazonSNSException("Test exception"));

        // Act
        topicManager.subscribeToTopic(TEST_TOPIC_ARN, TEST_PROTOCOL, TEST_ENDPOINT);
        // Assert: expect RuntimeException
    }

    /**
     * Test for pending confirmation scenario for email subscriptions.
     * 
     * FIXME: This test verifies the current behavior, but the implementation may need to be updated
     * to properly handle "pending confirmation" status for email subscriptions.
     */
    @Test
    public void testSubscribeToTopicWithPendingConfirmation() {
        // Arrange
        String pendingConfirmationArn = "pending confirmation";
        SubscribeResult mockResult = new SubscribeResult().withSubscriptionArn(pendingConfirmationArn);
        when(snsClient.subscribe(any(SubscribeRequest.class))).thenReturn(mockResult);

        // Act
        String resultSubscriptionArn = topicManager.subscribeToTopic(TEST_TOPIC_ARN, "email", "test@example.com");

        // Assert
        assertEquals("The returned subscription ARN should be 'pending confirmation'", 
                     pendingConfirmationArn, resultSubscriptionArn);
        
        // TODO: Add better handling for "pending confirmation" state when implementing email confirmation workflow
    }
}