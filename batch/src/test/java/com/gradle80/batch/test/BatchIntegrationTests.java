package com.gradle80.batch.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Integration tests for batch jobs.
 * This class tests the execution and results of the batch jobs in the application.
 */
@SpringBootTest
public class BatchIntegrationTests {

    /**
     * Testing utility for Spring Batch jobs
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    /**
     * The data processing job to test
     */
    @Autowired
    @Qualifier("dataProcessingJob")
    private Job dataProcessingJob;
    
    /**
     * The report generation job to test
     */
    @Autowired
    @Qualifier("reportGenerationJob")
    private Job reportGenerationJob;

    /**
     * Setup method to configure the job launcher test utils with the correct job
     */
    @BeforeClass
    public void setup() {
        // Default job is data processing job for the tests
        // Other jobs will be explicitly set in their test methods
        jobLauncherTestUtils.setJob(dataProcessingJob);
    }

    /**
     * Tests the data processing job execution.
     * Verifies that the job completes successfully and produces the expected results.
     * 
     * @throws Exception if any errors occur during job execution
     */
    @Test
    public void testDataProcessingJob() throws Exception {
        // Create job parameters with a unique run ID
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        
        // Execute the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        
        // Verify job execution status
        Assert.assertEquals(jobExecution.getExitStatus().getExitCode(), ExitStatus.COMPLETED.getExitCode(),
                "Data processing job did not complete successfully");
        
        // TODO: Add assertions for the expected outcomes of the job
        // For example, verify that the data was processed correctly by checking the database
        // or output files
    }

    /**
     * Tests the report generation job execution.
     * Verifies that the job completes successfully and generates the expected reports.
     * 
     * @throws Exception if any errors occur during job execution
     */
    @Test
    public void testReportGenerationJob() throws Exception {
        // Set the job to be tested
        jobLauncherTestUtils.setJob(reportGenerationJob);
        
        // Create job parameters with a unique run ID
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("reportDate", java.time.LocalDate.now().toString())
                .toJobParameters();
        
        // Execute the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        
        // Verify job execution status
        Assert.assertEquals(jobExecution.getExitStatus().getExitCode(), ExitStatus.COMPLETED.getExitCode(),
                "Report generation job did not complete successfully");
        
        // FIXME: Implement proper verification of report files generated
        // This should check the existence and content of generated report files
        
        // TODO: Add more detailed assertions to verify report content and format
    }
    
    /**
     * Additional test to verify steps can be run individually
     * This is useful for isolating issues in specific steps
     * 
     * @throws Exception if any errors occur during step execution
     */
    @Test
    public void testProcessDataStep() throws Exception {
        // Reset to data processing job
        jobLauncherTestUtils.setJob(dataProcessingJob);
        
        // Execute just the process data step
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("processDataStep");
        
        Assert.assertEquals(jobExecution.getExitStatus().getExitCode(), ExitStatus.COMPLETED.getExitCode(),
                "Process data step did not complete successfully");
    }
}