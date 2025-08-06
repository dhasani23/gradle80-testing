package com.gradle80.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gradle80.batch.util.BatchJobParametersBuilder;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BatchScheduler
 * 
 * Scheduler for batch jobs using Spring Scheduling. This class is responsible for
 * scheduling and triggering batch processing jobs at configured intervals.
 */
@Component
@EnableScheduling
public class BatchScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    @Qualifier("dataProcessingJob")
    private Job dataProcessingJob;
    
    @Autowired
    @Qualifier("reportGenerationJob")
    private Job reportGenerationJob;
    
    /**
     * Schedules the data processing job to run every day at 1:00 AM.
     * Uses a unique job parameter based on current timestamp to ensure job can be run multiple times.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleDataProcessingJob() {
        logger.info("Data processing job scheduled at: {}", new Date());
        try {
            // Create job parameters with unique run ID
            JobParameters jobParameters = new BatchJobParametersBuilder()
                    .addDate("executionDate", new Date())
                    .addString("source", "scheduler")
                    .toJobParameters();
            
            // Launch the job with parameters
            jobLauncher.run(dataProcessingJob, jobParameters);
            logger.info("Data processing job completed successfully");
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            logger.error("Error occurred during data processing job execution", e);
            // TODO: Implement proper error handling mechanism
        }
    }
    
    /**
     * Schedules the report generation job to run every Monday at 8:00 AM.
     * Generates weekly reports based on processed data.
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void scheduleReportGenerationJob() {
        logger.info("Report generation job scheduled at: {}", new Date());
        try {
            // Create job parameters with unique run ID and weekly flag
            JobParameters jobParameters = new BatchJobParametersBuilder()
                    .addDate("executionDate", new Date())
                    .addString("reportType", "weekly")
                    .addString("source", "scheduler")
                    .toJobParameters();
            
            // Launch the job with parameters
            jobLauncher.run(reportGenerationJob, jobParameters);
            logger.info("Report generation job completed successfully");
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            logger.error("Error occurred during report generation job execution", e);
            // FIXME: Current error handling doesn't include notification system for failed jobs
        }
    }
    
    // TODO: Implement dynamic scheduling mechanism to allow runtime schedule changes
    // TODO: Add monitoring and metrics collection for job execution statistics
}