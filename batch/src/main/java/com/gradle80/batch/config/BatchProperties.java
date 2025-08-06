package com.gradle80.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for batch jobs.
 * 
 * This class holds configuration values used across the batch processing
 * components, including chunk size for readers/writers, thread limits for
 * parallel processing, and scheduling configuration.
 */
@Component
@ConfigurationProperties(prefix = "batch")
public class BatchProperties {
    
    /**
     * Processing chunk size for batch operations.
     * Determines how many items will be processed in a single transaction.
     */
    private int chunkSize = 100; // Default value
    
    /**
     * Maximum number of threads for parallel batch processing.
     * Controls concurrency level for multi-threaded steps.
     */
    private int maxThreads = 4; // Default value
    
    /**
     * Flag to enable/disable scheduled batch job execution.
     * When false, jobs will only run when manually triggered.
     */
    private boolean scheduleEnabled = true; // Default value
    
    /**
     * Cron expression for scheduled batch job execution.
     * Follows standard Spring cron format.
     * 
     * Example: "0 0 2 * * ?" for daily execution at 2:00 AM
     */
    private String scheduleExpression = "0 0 * * * ?"; // Default: hourly
    
    // Getters and setters
    
    /**
     * Get the configured chunk size.
     * 
     * @return the chunk size for batch processing
     */
    public int getChunkSize() {
        return chunkSize;
    }
    
    /**
     * Set the chunk size for batch processing.
     * 
     * @param chunkSize the number of items to process in a single transaction
     */
    public void setChunkSize(int chunkSize) {
        // FIXME: Add validation to ensure chunk size is positive
        this.chunkSize = chunkSize;
    }
    
    /**
     * Get the maximum number of threads.
     * 
     * @return the maximum thread count for parallel processing
     */
    public int getMaxThreads() {
        return maxThreads;
    }
    
    /**
     * Set the maximum number of threads.
     * 
     * @param maxThreads the maximum thread count for parallel processing
     */
    public void setMaxThreads(int maxThreads) {
        // TODO: Consider system resources when setting thread count
        this.maxThreads = maxThreads;
    }
    
    /**
     * Check if job scheduling is enabled.
     * 
     * @return true if scheduling is enabled, false otherwise
     */
    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }
    
    /**
     * Enable or disable job scheduling.
     * 
     * @param scheduleEnabled true to enable scheduling, false to disable
     */
    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }
    
    /**
     * Get the cron expression for job scheduling.
     * 
     * @return the cron expression string
     */
    public String getScheduleExpression() {
        return scheduleExpression;
    }
    
    /**
     * Set the cron expression for job scheduling.
     * 
     * @param scheduleExpression the cron expression in Spring format
     */
    public void setScheduleExpression(String scheduleExpression) {
        // TODO: Add validation for cron expression format
        this.scheduleExpression = scheduleExpression;
    }
    
    @Override
    public String toString() {
        return "BatchProperties{" +
                "chunkSize=" + chunkSize +
                ", maxThreads=" + maxThreads +
                ", scheduleEnabled=" + scheduleEnabled +
                ", scheduleExpression='" + scheduleExpression + '\'' +
                '}';
    }
}