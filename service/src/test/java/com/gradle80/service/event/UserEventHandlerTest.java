package com.gradle80.service.event;

import com.gradle80.service.aws.SnsClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserEventHandler}.
 */
public class UserEventHandlerTest {

    @Mock
    private SnsClient snsClient;

    private UserEventHandler userEventHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userEventHandler = new UserEventHandler(snsClient);
    }

    @Test
    public void handleEvent_withUserCreatedEvent_shouldCallHandleUserCreatedEvent() {
        // Arrange
        Long userId = 123L;
        String username = "testuser";
        UserCreatedEvent event = new UserCreatedEvent(userId, username);
        when(snsClient.publishMessage(anyString(), anyString())).thenReturn("message-id-1");

        // Act
        userEventHandler.handleEvent(event);

        // Assert
        verify(snsClient).publishMessage(contains("User Created"), anyString());
    }

    @Test
    public void handleEvent_withUserDeletedEvent_shouldCallHandleUserDeletedEvent() {
        // Arrange
        Long userId = 456L;
        UserDeletedEvent event = new UserDeletedEvent(userId);
        when(snsClient.publishMessage(anyString(), anyString())).thenReturn("message-id-2");

        // Act
        userEventHandler.handleEvent(event);

        // Assert
        verify(snsClient).publishMessage(contains("User Deleted"), anyString());
    }

    @Test
    public void handleEvent_withUnsupportedEventType_shouldNotCallAnyHandler() {
        // Arrange
        Object unsupportedEvent = new Object();

        // Act
        userEventHandler.handleEvent(unsupportedEvent);

        // Assert
        verifyNoInteractions(snsClient);
    }

    @Test
    public void handleUserCreatedEvent_shouldPublishToSns() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(789L, "newuser");
        when(snsClient.publishMessage(anyString(), anyString())).thenReturn("message-id-3");

        // Act
        userEventHandler.handleUserCreatedEvent(event);

        // Assert
        verify(snsClient).publishMessage(eq("User Created: newuser"), anyString());
    }

    @Test
    public void handleUserDeletedEvent_shouldPublishToSns() {
        // Arrange
        UserDeletedEvent event = new UserDeletedEvent(101L);
        when(snsClient.publishMessage(anyString(), anyString())).thenReturn("message-id-4");

        // Act
        userEventHandler.handleUserDeletedEvent(event);

        // Assert
        verify(snsClient).publishMessage(eq("User Deleted: 101"), anyString());
    }

    @Test
    public void handleUserCreatedEvent_whenSnsClientThrowsException_shouldHandleGracefully() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(222L, "erroruser");
        when(snsClient.publishMessage(anyString(), anyString())).thenThrow(new RuntimeException("SNS error"));

        // Act - should not throw exception
        userEventHandler.handleUserCreatedEvent(event);

        // No assertion needed - test passes if no exception is thrown
    }
}