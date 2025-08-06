package com.gradle80.aws.sqs;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle80.aws.sqs.model.SqsMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link DefaultMessageProcessor}.
 */
public class DefaultMessageProcessorTest {
    
    private DefaultMessageProcessor messageProcessor;
    
    private ObjectMapper objectMapper;
    
    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        messageProcessor = new DefaultMessageProcessor(objectMapper);
    }
    
    @Test
    public void testProcessMessage_withValidMessage_shouldNotThrowException() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();
        attributes.put("messageType", "TEST");
        SqsMessage message = new SqsMessage("test-id", "receipt-handle", "{\"name\":\"Test\"}", attributes);
        
        // Act & Assert (should not throw exception)
        messageProcessor.processMessage(message);
    }
    
    @Test
    public void testProcessMessage_withNullMessage_shouldNotThrowException() {
        // Act & Assert (should not throw exception)
        messageProcessor.processMessage(null);
    }
    
    @Test
    public void testConvertMessageToType_withValidJson_shouldReturnDeserializedObject() {
        // Arrange
        String json = "{\"name\":\"Test User\",\"age\":30}";
        SqsMessage message = new SqsMessage("test-id", "receipt-handle", json, null);
        
        // Act
        TestUser result = messageProcessor.convertMessageToType(message, TestUser.class);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals(30, result.getAge());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertMessageToType_withInvalidJson_shouldThrowException() {
        // Arrange
        String json = "{invalid-json}";
        SqsMessage message = new SqsMessage("test-id", "receipt-handle", json, null);
        
        // Act (should throw exception)
        messageProcessor.convertMessageToType(message, TestUser.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertMessageToType_withEmptyBody_shouldThrowException() {
        // Arrange
        SqsMessage message = new SqsMessage("test-id", "receipt-handle", "", null);
        
        // Act (should throw exception)
        messageProcessor.convertMessageToType(message, TestUser.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertMessageToType_withNullMessage_shouldThrowException() {
        // Act (should throw exception)
        messageProcessor.convertMessageToType(null, TestUser.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertMessageToType_withNullTargetClass_shouldThrowException() {
        // Arrange
        SqsMessage message = new SqsMessage("test-id", "receipt-handle", "{}", null);
        
        // Act (should throw exception)
        messageProcessor.convertMessageToType(message, null);
    }
    
    /**
     * Simple test class for JSON deserialization.
     */
    static class TestUser {
        private String name;
        private int age;
        
        public TestUser() {
            // Default constructor for Jackson
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getAge() {
            return age;
        }
        
        public void setAge(int age) {
            this.age = age;
        }
    }
}