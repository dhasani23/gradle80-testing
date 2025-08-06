package com.gradle80.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gradle80.batch.item.reader.DataProcessingItemReader;
import com.gradle80.batch.item.processor.DataProcessingItemProcessor;
import com.gradle80.batch.item.writer.DatabaseItemWriter;

/**
 * Definition for data processing batch job.
 * This class configures a job that reads data from a source,
 * processes it, and writes it to a database.
 */
@Configuration
public class DataProcessingJob {

    /**
     * Job builder factory for creating job definitions
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * Step builder factory for creating step definitions
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Reader component that handles data extraction
     */
    private final DataProcessingItemReader itemReader;

    /**
     * Processor component that handles data transformation
     */
    private final DataProcessingItemProcessor itemProcessor;

    /**
     * Writer component that handles data persistence
     */
    private final DatabaseItemWriter itemWriter;

    /**
     * Chunk size for batch processing
     */
    @Value("${batch.chunk-size:100}")
    private int chunkSize;

    /**
     * Constructor injection for all required dependencies
     * 
     * @param jobBuilderFactory Factory for building jobs
     * @param stepBuilderFactory Factory for building steps
     * @param itemReader Data source reader
     * @param itemProcessor Data processor
     * @param itemWriter Database writer
     */
    @Autowired
    public DataProcessingJob(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            DataProcessingItemReader itemReader,
            DataProcessingItemProcessor itemProcessor,
            DatabaseItemWriter itemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    /**
     * Creates and configures the data processing job.
     * 
     * @return configured Job instance
     */
    @Bean
    public Job dataProcessingJob() {
        return jobBuilderFactory.get("dataProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(processDataStep())
                // TODO: Add job listener for better monitoring and notification
                // .listener(jobCompletionListener)
                .build();
    }

    /**
     * Creates and configures the data processing step.
     * This step reads data from the source, processes it, and writes to the database.
     * 
     * @return configured Step instance
     */
    @Bean
    public Step processDataStep() {
        return stepBuilderFactory.get("processDataStep")
                .<Object, Object>chunk(chunkSize)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                // FIXME: Add proper error handling for step failures
                // .faultTolerant()
                // .skipLimit(10)
                // .skip(Exception.class)
                .build();
    }
}