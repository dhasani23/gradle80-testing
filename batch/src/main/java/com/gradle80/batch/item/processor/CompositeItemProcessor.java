package com.gradle80.batch.item.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * A composite item processor that chains multiple {@link ItemProcessor}s together.
 * <p>
 * This processor allows a sequence of discrete business processing steps to be
 * applied to an item, with the output of one processor becoming the input of 
 * the next. Processing stops if any processor returns null.
 * </p>
 *
 * @author Gradle80
 * @version 1.0
 */
@Component
public class CompositeItemProcessor implements ItemProcessor<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(CompositeItemProcessor.class);
    
    /**
     * List of item processors to be executed in sequence
     */
    private List<ItemProcessor<Object, Object>> delegates;

    /**
     * Default constructor initializing an empty processor chain
     */
    public CompositeItemProcessor() {
        this.delegates = new ArrayList<>();
    }

    /**
     * Constructor with predefined list of processors
     * 
     * @param delegates the list of processors to delegate to
     */
    public CompositeItemProcessor(List<ItemProcessor<Object, Object>> delegates) {
        this.delegates = delegates;
    }

    /**
     * Sets the list of delegates for this composite processor
     * 
     * @param delegates the list of processors to delegate to
     */
    public void setDelegates(List<ItemProcessor<Object, Object>> delegates) {
        this.delegates = delegates;
    }
    
    /**
     * Gets the list of delegates
     * 
     * @return the list of item processors
     */
    public List<ItemProcessor<Object, Object>> getDelegates() {
        return delegates;
    }
    
    /**
     * Add a processor to the end of the chain
     * 
     * @param processor the processor to add
     */
    public void addProcessor(ItemProcessor<Object, Object> processor) {
        this.delegates.add(processor);
    }

    /**
     * Processes an item by passing it through each of the delegate processors in sequence.
     * <p>
     * If a delegate processor returns null, the chain terminates early and null is returned.
     * Otherwise, the output from each processor is passed as input to the next processor.
     * </p>
     *
     * @param item the item to be processed
     * @return the final transformed item, or null if processing should stop
     * @throws Exception if an error occurs during processing
     */
    @Override
    public Object process(Object item) throws Exception {
        if (item == null) {
            logger.debug("Received null item, skipping composite processing");
            return null;
        }
        
        if (delegates == null || delegates.isEmpty()) {
            logger.warn("No delegate processors configured, returning item unchanged");
            return item;
        }
        
        Object result = item;
        
        try {
            // Apply each processor in sequence
            for (ItemProcessor<Object, Object> processor : delegates) {
                logger.debug("Applying processor: {}", processor.getClass().getSimpleName());
                
                result = processor.process(result);
                
                // If any processor returns null, stop the chain and return null
                if (result == null) {
                    logger.debug("Processor {} returned null, terminating chain", processor.getClass().getSimpleName());
                    return null;
                }
            }
            
            logger.debug("Successfully completed processing chain for item");
            return result;
            
        } catch (Exception e) {
            logger.error("Error in composite processor chain: {}", e.getMessage(), e);
            // FIXME: Implement more robust error handling strategy
            // TODO: Add metrics for chain performance monitoring
            throw e; // Re-throw to fail the chunk
        }
    }
}