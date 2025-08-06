package com.gradle80.batch.monitor;

import com.gradle80.batch.repository.BatchJobExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for monitoring batch jobs.
 * 
 * This service provides functionality to monitor the status and progress of batch jobs
 * running in the system. It leverages Spring Batch's JobExplorer for real-time monitoring
 * and our custom BatchJobExecutionRepository for historical data.
 */
@Service
public class BatchJobMonitor {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobMonitor.class);

    /**
     * Job explorer
     */
    private final JobExplorer jobExplorer;
    
    /**
     * Execution repository
     */
    private final BatchJobExecutionRepository executionRepository;

    /**
     * Constructor with dependency injection
     * 
     * @param jobExplorer Spring Batch job explorer
     * @param executionRepository Custom repository for job execution data
     */
    @Autowired
    public BatchJobMonitor(JobExplorer jobExplorer, BatchJobExecutionRepository executionRepository) {
        this.jobExplorer = jobExplorer;
        this.executionRepository = executionRepository;
    }

    /**
     * Find running jobs in the system.
     * This method returns all job executions that are currently in progress.
     * 
     * @return List of running job executions
     */
    public List<JobExecution> getRunningJobs() {
        // Get all job executions that are in a running state
        List<String> jobNames = jobExplorer.getJobNames();
        
        logger.debug("Checking {} job types for running executions", jobNames.size());
        
        // Use Java 8 streams to collect all running jobs
        return jobNames.stream()
            .flatMap(jobName -> jobExplorer.findRunningJobExecutions(jobName).stream())
            .collect(Collectors.toList());
    }

    /**
     * Check the status of a specific job execution.
     * 
     * @param executionId The execution ID to check
     * @return The current status of the job execution
     * @throws IllegalArgumentException if the execution ID is not found
     */
    public BatchStatus getJobExecutionStatus(long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        
        if (jobExecution == null) {
            logger.error("No job execution found with ID: {}", executionId);
            throw new IllegalArgumentException("No job execution found with ID: " + executionId);
        }
        
        BatchStatus status = jobExecution.getStatus();
        logger.debug("Job execution {} has status: {}", executionId, status);
        return status;
    }
    
    /**
     * Get detailed information about a specific job execution.
     * 
     * @param executionId The execution ID to retrieve
     * @return The job execution details
     * @throws IllegalArgumentException if the execution ID is not found
     */
    public JobExecution getJobExecutionDetails(long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        
        if (jobExecution == null) {
            logger.error("No job execution found with ID: {}", executionId);
            throw new IllegalArgumentException("No job execution found with ID: " + executionId);
        }
        
        // TODO: Enhance with additional metrics and step execution details
        return jobExecution;
    }
    
    /**
     * Find recent executions of a specific job.
     * 
     * @param jobName The name of the job
     * @param limit Maximum number of executions to return
     * @return List of recent job executions
     */
    public List<JobExecution> getRecentJobExecutions(String jobName, int limit) {
        // FIXME: Add pagination support for large result sets
        List<JobExecution> executions = executionRepository.findJobExecutions(jobName);
        
        // Apply limit if needed
        if (executions.size() > limit) {
            return executions.subList(0, limit);
        }
        
        return executions;
    }
}