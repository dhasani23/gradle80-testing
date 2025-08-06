package com.gradle80.batch.item.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gradle80.common.validation.ValidationException;
import com.gradle80.common.validation.Validator;

/**
 * Item processor responsible for data validation in batch jobs.
 * <p>
 * This processor uses a {@link Validator} to validate input items before they
 * are passed to the next step in the processing chain. Items that fail validation
 * will be filtered out from the processing stream.
 * </p>
 *
 * @author Gradle80
 * @version 1.0
 */
@Component
public class ValidationItemProcessor implements ItemProcessor<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(ValidationItemProcessor.class);
    
    /**
     * Validator service used for validating items
     */
    private final Validator<Object> validator;

    /**
     * Constructs a new processor with the required validator.
     *
     * @param validator the service for validating data items
     */
    @Autowired
    public ValidationItemProcessor(Validator<Object> validator) {
        this.validator = validator;
    }

    /**
     * Processes the input item by validating it against business rules.
     * <p>
     * This method validates the input data according to business requirements.
     * If an item fails validation, it returns null which will cause the item
     * to be skipped (not passed to the writer).
     * </p>
     *
     * @param item the item to be validated
     * @return the validated item, or null if validation fails
     * @throws Exception if an error occurs during processing
     */
    @Override
    public Object process(Object item) throws Exception {
        if (item == null) {
            logger.warn("Received null item, skipping validation");
            return null;
        }
        
        logger.debug("Validating item of type: {}", item.getClass().getSimpleName());
        
        try {
            // Apply validation logic using the validator service
            validator.validate(item);
            
            logger.debug("Item validation successful");
            
            // TODO: Add metrics collection for validation success rate
            // TODO: Implement conditional validation based on item type or attributes
            
            return item;
        } catch (ValidationException e) {
            // Handle validation errors - log and filter out the item
            logger.warn("Validation failed for item: {}", e.getMessage());
            
            // FIXME: Consider implementing a validation error handler to report batch validation issues
            // FIXME: Add configuration option to fail the entire chunk on validation errors
            
            return null;
        } catch (IllegalArgumentException e) {
            // Handle validation errors due to invalid arguments
            logger.warn("Invalid argument during validation: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("Error during item validation: {}", e.getMessage(), e);
            
            // FIXME: Determine proper error handling strategy - currently errors are just logged 
            // and items are filtered, but we might want to fail the job in some cases
            
            return null;
        }
    }
}