package com.gradle80.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gradle80.batch.item.reader.JdbcItemReader;
import com.gradle80.batch.item.processor.ValidationItemProcessor;
import com.gradle80.batch.item.writer.FileItemWriter;

/**
 * Definition for report generation batch job.
 * This class configures a job that extracts data from a database,
 * validates it, and outputs formatted reports to files.
 */
@Configuration
public class ReportGenerationJob {

    /**
     * Job builder factory for creating job definitions
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * Step builder factory for creating step definitions
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Reader component that extracts data from database
     */
    private final JdbcItemReader<Object> itemReader;

    /**
     * Processor component that validates report data
     */
    private final ValidationItemProcessor itemProcessor;

    /**
     * Writer component that outputs reports to files
     */
    private final FileItemWriter<Object> itemWriter;

    /**
     * Chunk size for batch processing
     */
    @Value("${batch.report.chunk-size:50}")
    private int chunkSize;
    
    /**
     * Maximum number of errors before failing
     */
    @Value("${batch.report.skip-limit:10}")
    private int skipLimit;

    /**
     * Constructor injection for all required dependencies
     * 
     * @param jobBuilderFactory Factory for building jobs
     * @param stepBuilderFactory Factory for building steps
     * @param itemReader Database report reader
     * @param itemProcessor Report validator
     * @param itemWriter Report file writer
     */
    @Autowired
    public ReportGenerationJob(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JdbcItemReader<Object> itemReader,
            ValidationItemProcessor itemProcessor,
            FileItemWriter<Object> itemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    /**
     * Creates and configures the report generation job.
     * This job extracts data from the database, validates it,
     * and writes formatted reports to files.
     * 
     * @return configured Job instance
     */
    @Bean
    public Job reportGenerationJob() {
        return jobBuilderFactory.get("reportGenerationJob")
                .incrementer(new RunIdIncrementer())
                .start(generateReportStep())
                // TODO: Add job completion notification listener
                // .listener(reportCompletionListener)
                .build();
    }

    /**
     * Creates and configures the report generation step.
     * This step handles the extraction, validation, and file output 
     * of report data.
     * 
     * @return configured Step instance
     */
    @Bean
    public Step generateReportStep() {
        // Explicitly create and cast the writer to ensure type compatibility
        ItemWriter<Object> writer = itemWriter.createWriter();
        
        return stepBuilderFactory.get("generateReportStep")
                .<Object, Object>chunk(chunkSize)
                .reader(itemReader.createReader())
                .processor(itemProcessor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(Exception.class)
                // FIXME: Implement more specific exception handling strategy
                // Consider handling different exception types differently
                .build();
    }
}