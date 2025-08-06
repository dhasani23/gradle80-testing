package com.gradle80.batch.test;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test configuration for batch integration tests.
 * Provides necessary beans for testing batch jobs.
 */
@Configuration
public class BatchTestConfig {

    /**
     * Creates a JobLauncherTestUtils bean for testing batch jobs.
     * This bean is used to launch jobs in test environment.
     *
     * @return JobLauncherTestUtils instance
     */
    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}