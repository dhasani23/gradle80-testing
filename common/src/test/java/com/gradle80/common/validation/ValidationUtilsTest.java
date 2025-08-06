package com.gradle80.common.validation;

import com.gradle80.common.exception.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for ValidationUtils utility methods.
 */
public class ValidationUtilsTest {

    @Test
    public void testValidateNotNull_WithNonNullValue_ShouldNotThrowException() {
        // Given
        Object nonNullObject = "test";
        
        // When/Then
        ValidationUtils.validateNotNull(nonNullObject, "testField");
        // No exception thrown means test passes
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateNotNull_WithNullValue_ShouldThrowException() {
        // Given
        Object nullObject = null;
        
        // When
        ValidationUtils.validateNotNull(nullObject, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test
    public void testValidateNotEmpty_WithNonEmptyString_ShouldNotThrowException() {
        // Given
        String nonEmptyString = "test";
        
        // When/Then
        ValidationUtils.validateNotEmpty(nonEmptyString, "testField");
        // No exception thrown means test passes
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateNotEmpty_WithEmptyString_ShouldThrowException() {
        // Given
        String emptyString = "";
        
        // When
        ValidationUtils.validateNotEmpty(emptyString, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateNotEmpty_WithWhitespaceString_ShouldThrowException() {
        // Given
        String whitespaceString = "   ";
        
        // When
        ValidationUtils.validateNotEmpty(whitespaceString, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateNotEmpty_WithNullString_ShouldThrowException() {
        // Given
        String nullString = null;
        
        // When
        ValidationUtils.validateNotEmpty(nullString, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test
    public void testValidateEmail_WithValidEmail_ShouldNotThrowException() {
        // Given
        String validEmail = "test@example.com";
        
        // When/Then
        ValidationUtils.validateEmail(validEmail);
        // No exception thrown means test passes
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateEmail_WithInvalidEmail_ShouldThrowException() {
        // Given
        String invalidEmail = "test@";
        
        // When
        ValidationUtils.validateEmail(invalidEmail);
        
        // Then: expect ValidationException
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateEmail_WithEmptyEmail_ShouldThrowException() {
        // Given
        String emptyEmail = "";
        
        // When
        ValidationUtils.validateEmail(emptyEmail);
        
        // Then: expect ValidationException
    }
    
    @Test
    public void testValidatePositive_WithPositiveValue_ShouldNotThrowException() {
        // Given
        int positiveValue = 5;
        
        // When/Then
        ValidationUtils.validatePositive(positiveValue, "testField");
        // No exception thrown means test passes
    }
    
    @Test(expected = ValidationException.class)
    public void testValidatePositive_WithZero_ShouldThrowException() {
        // Given
        int zeroValue = 0;
        
        // When
        ValidationUtils.validatePositive(zeroValue, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test(expected = ValidationException.class)
    public void testValidatePositive_WithNegativeValue_ShouldThrowException() {
        // Given
        int negativeValue = -5;
        
        // When
        ValidationUtils.validatePositive(negativeValue, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test
    public void testValidateRange_WithValueInRange_ShouldNotThrowException() {
        // Given
        int valueInRange = 5;
        
        // When/Then
        ValidationUtils.validateRange(valueInRange, 1, 10, "testField");
        // No exception thrown means test passes
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateRange_WithValueBelowRange_ShouldThrowException() {
        // Given
        int valueBelowRange = 0;
        
        // When
        ValidationUtils.validateRange(valueBelowRange, 1, 10, "testField");
        
        // Then: expect ValidationException
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateRange_WithValueAboveRange_ShouldThrowException() {
        // Given
        int valueAboveRange = 11;
        
        // When
        ValidationUtils.validateRange(valueAboveRange, 1, 10, "testField");
        
        // Then: expect ValidationException
    }
}