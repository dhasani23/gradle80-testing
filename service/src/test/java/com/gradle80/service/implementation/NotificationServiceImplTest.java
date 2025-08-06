package com.gradle80.service.implementation;

import com.gradle80.client.SnsClient;
import com.gradle80.entity.Notification;
import com.gradle80.mapper.NotificationMapper;
import com.gradle80.model.NotificationRequest;
import com.gradle80.model.NotificationResponse;
import com.gradle80.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private SnsClient snsClient;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository, notificationMapper, snsClient);
    }

    @Test
    void sendNotification_ShouldSaveAndPublish() {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setTitle("Test Title");
        request.setMessage("Test Message");
        request.setType("EMAIL");
        request.setTopicArn("arn:aws:sns:us-east-1:123456789012:my-topic");

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");

        NotificationResponse expectedResponse = new NotificationResponse();
        expectedResponse.setId(1L);
        expectedResponse.setTitle("Test Title");

        when(notificationMapper.requestToEntity(request)).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(snsClient.publishMessage(anyString(), anyString(), anyMap())).thenReturn("message-123");
        when(notificationMapper.entityToResponse(notification)).thenReturn(expectedResponse);

        // Act
        NotificationResponse response = notificationService.sendNotification(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        
        verify(notificationMapper).requestToEntity(request);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(snsClient).publishMessage(eq(request.getTopicArn()), eq(request.getMessage()), any());
        verify(notificationMapper).entityToResponse(notification);
        
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getAllValues().get(0);
        
        assertFalse(savedNotification.isRead());
        assertNotNull(savedNotification.getCreatedAt());
    }

    @Test
    void getUserNotifications_ShouldReturnSortedList() {
        // Arrange
        Long userId = 1L;
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setCreatedAt(100L);
        
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setCreatedAt(200L);
        
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        
        NotificationResponse response1 = new NotificationResponse();
        response1.setId(1L);
        
        NotificationResponse response2 = new NotificationResponse();
        response2.setId(2L);
        
        when(notificationRepository.findByUserId(userId)).thenReturn(notifications);
        when(notificationMapper.entityToResponse(notification1)).thenReturn(response1);
        when(notificationMapper.entityToResponse(notification2)).thenReturn(response2);
        
        // Act
        List<NotificationResponse> result = notificationService.getUserNotifications(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        // Should be sorted by createdAt desc (notification2 first, then notification1)
        assertEquals(response2, result.get(0));
        assertEquals(response1, result.get(1));
    }

    @Test
    void markAsRead_ShouldUpdateNotification() {
        // Arrange
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setRead(false);
        
        NotificationResponse expectedResponse = new NotificationResponse();
        expectedResponse.setId(notificationId);
        expectedResponse.setRead(true);
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.entityToResponse(notification)).thenReturn(expectedResponse);
        
        // Act
        NotificationResponse response = notificationService.markAsRead(notificationId);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isRead());
        
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();
        
        assertTrue(savedNotification.isRead());
        assertNotNull(savedNotification.getReadAt());
    }
    
    @Test
    void markAsRead_NotificationNotFound_ShouldReturnNull() {
        // Arrange
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        
        // Act
        NotificationResponse response = notificationService.markAsRead(notificationId);
        
        // Assert
        assertNull(response);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}