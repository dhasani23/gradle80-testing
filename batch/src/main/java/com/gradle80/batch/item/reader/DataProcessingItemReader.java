package com.gradle80.batch.item.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Item reader implementation for data processing batch jobs.
 * Reads items from a repository in batches to efficiently process large datasets.
 * This reader implements both ItemReader and ItemStream interfaces to support
 * stateful reading and to participate in the Spring Batch lifecycle.
 * 
 * @author gradle80
 */
@Component
public class DataProcessingItemReader<T> implements ItemReader<T>, ItemStream {

    /**
     * Data repository used to fetch entities for processing
     */
    private final EntityRepository repository;
    
    /**
     * Number of items to fetch in each batch from the repository
     */
    private int batchSize;
    
    /**
     * Current position in the data set
     */
    private int currentIndex;
    
    /**
     * Cache of items read from repository to minimize database access
     */
    private List<T> items;
    
    /**
     * Total number of items to process
     */
    private Integer totalItems;
    
    /**
     * Flag indicating if all items have been read
     */
    private boolean exhausted = false;
    
    /**
     * Constructor with repository dependency
     * 
     * @param repository the data repository to read from
     */
    @Autowired
    public DataProcessingItemReader(EntityRepository repository) {
        this.repository = repository;
        this.batchSize = 100; // Default batch size
        this.currentIndex = 0;
    }
    
    /**
     * Set the size of batches to read from the repository
     * 
     * @param batchSize number of records to fetch in each batch
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    /**
     * Reads the next item from the data set.
     * This method is called repeatedly by the Spring Batch framework until it returns null.
     * 
     * @return the next item to be processed, or null if there are no more items
     */
    @Override
    public T read() {
        // If we've exhausted all data, return null to signal end of data
        if (exhausted) {
            return null;
        }
        
        // If we need to load a new batch of items
        if (items == null || currentIndex >= items.size()) {
            // Load next batch from repository
            loadNextBatch();
            
            // If no items were loaded, we've reached the end
            if (items == null || items.isEmpty()) {
                exhausted = true;
                return null;
            }
            
            // Reset index for the new batch
            currentIndex = 0;
        }
        
        // Return the current item and increment the index
        return items.get(currentIndex++);
    }
    
    /**
     * Loads the next batch of items from the repository
     */
    @SuppressWarnings("unchecked")
    private void loadNextBatch() {
        // Calculate offset based on current position
        int offset = currentIndex;
        
        // Get items from repository
        // This assumes the repository has a method to fetch items with pagination
        items = (List<T>) repository.findAll(offset, batchSize);
    }
    
    /**
     * Initializes the reader with execution context.
     * This method is called by the Spring Batch framework before reading begins.
     * 
     * @param executionContext the batch job execution context
     * @throws ItemStreamException if there's an error during initialization
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // Reset state
        this.currentIndex = 0;
        this.exhausted = false;
        this.items = null;
        
        // Check if we're restarting and have a saved position
        if (executionContext.containsKey("current.index")) {
            currentIndex = executionContext.getInt("current.index");
        }
        
        // Initialize total count if needed for progress reporting
        // TODO: Consider making this calculation optional to improve performance
        if (totalItems == null) {
            totalItems = repository.count();
        }
    }
    
    /**
     * Updates the execution context with the current state.
     * This allows the job to restart from this point if it fails.
     * 
     * @param executionContext the batch job execution context
     * @throws ItemStreamException if there's an error during update
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt("current.index", currentIndex);
    }
    
    /**
     * Performs cleanup after reading is complete.
     * This method is called by the Spring Batch framework after reading is finished.
     * 
     * @throws ItemStreamException if there's an error during cleanup
     */
    @Override
    public void close() throws ItemStreamException {
        // Release resources
        items = null;
    }
    
    /**
     * Get the current position in the data set.
     * Useful for monitoring and reporting job progress.
     * 
     * @return current position in the data set
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Interface for repositories that can be used with this reader.
     * This allows the reader to work with different repository implementations.
     */
    public interface EntityRepository {
        /**
         * Find a batch of entities with pagination
         * 
         * @param offset position to start from
         * @param limit maximum number of records to return
         * @return list of entities
         */
        List<?> findAll(int offset, int limit);
        
        /**
         * Count total entities available
         * 
         * @return total count of available entities
         */
        int count();
    }
}