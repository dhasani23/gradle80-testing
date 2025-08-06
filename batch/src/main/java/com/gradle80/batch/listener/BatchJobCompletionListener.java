package com.gradle80.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * Listener for batch job completion events.
 * 
 * This listener provides hooks to perform actions before and after a batch job executes.
 * It logs relevant information about job execution, including:
 * - Start/end times
 * - Execution status
 * - Job parameters
 * - Any exceptions that occurred
 */
@Component
public class BatchJobCompletionListener implements JobExecutionListener {
    
    /**
     * Logging utility
     */
    private final Logger logger = LoggerFactory.getLogger(BatchJobCompletionListener.class);
    
    /**
     * Pre-job preparation.
     * 
     * This method is called before a job begins execution. It logs job information
     * and performs any necessary setup.
     * 
     * @param jobExecution the execution context for the job
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("==== JOB STARTING: {} ====", jobExecution.getJobInstance().getJobName());
        logger.info("Job Parameters: {}", jobExecution.getJobParameters());
        logger.info("Start Time: {}", jobExecution.getStartTime());
        
        // Record job start in performance metrics
        // TODO: Implement integration with monitoring system
        
        // Prepare any resources needed for job execution
        try {
            // Initialize job resources if needed
        } catch (Exception e) {
            logger.error("Error during job preparation", e);
            // FIXME: Consider proper error handling strategy
        }
    }
    
    /**
     * Post-job processing.
     * 
     * This method is called after a job completes execution, regardless of its completion status.
     * It logs job results, cleans up resources, and performs any necessary post-processing.
     * 
     * @param jobExecution the execution context for the job
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        Date endTime = jobExecution.getEndTime();
        Date startTime = jobExecution.getStartTime();
        long duration = 0;
        
        if (endTime != null && startTime != null) {
            duration = endTime.getTime() - startTime.getTime();
        }
        
        logger.info("==== JOB FINISHED: {} ====", jobExecution.getJobInstance().getJobName());
        logger.info("Status: {}", jobExecution.getStatus());
        logger.info("Duration: {} ms", duration);
        
        // Log detailed execution information
        if (jobExecution.getFailureExceptions().size() > 0) {
            logger.error("Job had {} exceptions", jobExecution.getFailureExceptions().size());
            for (Throwable exception : jobExecution.getFailureExceptions()) {
                logger.error("Exception: ", exception);
            }
        }
        
        // Perform cleanup of any resources
        try {
            // Clean up resources allocated during job
        } catch (Exception e) {
            logger.warn("Error during job cleanup", e);
        }
        
        // Publish job completion metrics
        // TODO: Add integration with notification systems for job completion status
    }
}