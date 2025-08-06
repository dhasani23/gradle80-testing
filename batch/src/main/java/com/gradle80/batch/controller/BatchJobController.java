package com.gradle80.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for batch job management.
 * This controller provides endpoints to trigger batch jobs manually.
 */
@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job dataProcessingJob;
    private final Job reportGenerationJob;

    /**
     * Constructor for dependency injection.
     * 
     * @param jobLauncher The Spring Batch job launcher
     * @param dataProcessingJob The data processing batch job
     * @param reportGenerationJob The report generation batch job
     */
    @Autowired
    public BatchJobController(
            JobLauncher jobLauncher,
            @Qualifier("dataProcessingJob") Job dataProcessingJob,
            @Qualifier("reportGenerationJob") Job reportGenerationJob) {
        this.jobLauncher = jobLauncher;
        this.dataProcessingJob = dataProcessingJob;
        this.reportGenerationJob = reportGenerationJob;
    }

    /**
     * Endpoint to trigger the data processing job.
     * 
     * @param requestParams Map containing job parameters
     * @return ResponseEntity with job execution status information
     */
    @PostMapping("/process")
    public ResponseEntity<?> launchDataProcessingJob(@RequestBody Map<String, String> requestParams) {
        try {
            JobParameters jobParameters = buildJobParametersFromRequest(requestParams);
            JobExecution jobExecution = jobLauncher.run(dataProcessingJob, jobParameters);
            
            Map<String, Object> response = new HashMap<>();
            response.put("jobId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", jobExecution.getStartTime());
            
            return ResponseEntity.ok(response);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | 
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error launching data processing job: " + e.getMessage());
        }
    }

    /**
     * Endpoint to trigger the report generation job.
     * 
     * @param requestParams Map containing job parameters
     * @return ResponseEntity with job execution status information
     */
    @PostMapping("/report")
    public ResponseEntity<?> launchReportGenerationJob(@RequestBody Map<String, String> requestParams) {
        try {
            JobParameters jobParameters = buildJobParametersFromRequest(requestParams);
            JobExecution jobExecution = jobLauncher.run(reportGenerationJob, jobParameters);
            
            Map<String, Object> response = new HashMap<>();
            response.put("jobId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", jobExecution.getStartTime());
            
            return ResponseEntity.ok(response);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | 
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error launching report generation job: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to convert request parameters to job parameters.
     * Always adds a timestamp parameter to ensure unique job instances.
     * 
     * @param requestParams Map of request parameters
     * @return JobParameters object for job execution
     */
    private JobParameters buildJobParametersFromRequest(Map<String, String> requestParams) {
        JobParametersBuilder builder = new JobParametersBuilder();
        
        // Add all request parameters
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            builder.addString(entry.getKey(), entry.getValue());
        }
        
        // Add timestamp to ensure unique job instance
        builder.addDate("timestamp", new Date());
        
        // TODO: Add validation for required job parameters
        // FIXME: Handle parameter type conversion more robustly
        
        return builder.toJobParameters();
    }
}