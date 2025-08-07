package com.gradle80.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the batch processing module.
 * 
 * This class serves as the entry point for Spring Boot batch jobs.
 * It enables Spring Batch processing features and scheduling capabilities.
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class BatchApplication {

    /**
     * Main method to start the batch application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}