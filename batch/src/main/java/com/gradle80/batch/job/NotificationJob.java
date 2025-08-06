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
import org.springframework.transaction.PlatformTransactionManager;

import com.gradle80.batch.item.processor.CompositeItemProcessor;
import com.gradle80.batch.item.reader.JdbcItemReader;
import com.gradle80.batch.item.writer.SqsItemWriter;
import com.gradle80.batch.listener.BatchJobCompletionListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Definition for notification batch job.
 * This job reads data from database, processes it through composite processors,
 * and sends notifications through AWS SQS.
 */
@Configuration
@Slf4j
public class NotificationJob {

    // Job and step factories
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    // Item handlers
    private final JdbcItemReader itemReader;
    private final CompositeItemProcessor itemProcessor;
    private final SqsItemWriter itemWriter;
    
    @Value("${batch.notification.chunk-size:100}")
    private int chunkSize;
    
    /**
     * Constructor with dependency injection.
     *
     * @param jobBuilderFactory Factory for creating job builders
     * @param stepBuilderFactory Factory for creating step builders
     * @param itemReader Database reader component
     * @param itemProcessor Composite processor for notification data
     * @param itemWriter SQS writer for sending notifications
     */
    @Autowired
    public NotificationJob(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JdbcItemReader itemReader,
            CompositeItemProcessor itemProcessor,
            SqsItemWriter itemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    /**
     * Defines the notification job with a single step.
     * The job includes an incrementer to allow multiple runs and a completion listener.
     *
     * @param listener Job completion listener for notifications
     * @return Configured notification job
     */
    @Bean
    public Job notificationJob(BatchJobCompletionListener listener) {
        log.info("Initializing notification job");
        
        return jobBuilderFactory.get("notificationJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(sendNotificationStep())
                .end()
                .build();
    }

    /**
     * Defines the step for sending notifications.
     * This step handles reading from database, processing the data,
     * and sending messages to SQS.
     *
     * @return Configured step for notification processing
     */
    @Bean
    public Step sendNotificationStep() {
        log.debug("Configuring notification step with chunk size: {}", chunkSize);
        
        // TODO: Add error handling for SQS communication failures
        // FIXME: Need to handle duplicate notifications when job restarts
        
        return stepBuilderFactory.get("sendNotificationStep")
                .<Object, Object>chunk(chunkSize)
                .reader(itemReader.createReader())
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }
}