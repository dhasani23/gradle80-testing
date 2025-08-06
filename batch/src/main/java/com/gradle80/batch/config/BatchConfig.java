package com.gradle80.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.gradle80.batch.listener.BatchJobCompletionListener;

/**
 * Spring Batch configuration class that sets up the batch job infrastructure.
 * 
 * This class configures essential batch components including:
 * - Job repository for storing job metadata
 * - Transaction manager for handling job transactions
 * - Database initialization for batch tables
 */
@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class BatchConfig extends DefaultBatchConfigurer {

    /**
     * Job builder factory provided by Spring Batch
     */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    /**
     * Step builder factory provided by Spring Batch
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    /**
     * Database connection for batch metadata
     */
    @Autowired
    private DataSource dataSource;
    
    /**
     * Initializes the datasource with Spring Batch schema.
     * 
     * @param dataSource The datasource to initialize
     * @return DataSourceInitializer configured to create batch tables
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        
        // Add the Spring Batch schema script
        databasePopulator.addScript(new ClassPathResource("schema-batch.sql"));
        databasePopulator.setIgnoreFailedDrops(true);
        
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        
        // TODO: Make this configurable via properties
        initializer.setEnabled(true);
        
        return initializer;
    }
    
    /**
     * Configures the job repository for storing job metadata.
     * 
     * @return JobRepository used by Spring Batch
     * @throws Exception if repository creation fails
     */
    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager());
        
        // FIXME: Isolation levels might need adjustment based on database vendor
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factory.setTablePrefix("BATCH_");
        
        // For production environments, consider setting these to true
        factory.setValidateTransactionState(false);
        
        return factory.getObject();
    }
    
    /**
     * Configures the transaction manager for batch operations.
     * 
     * @return PlatformTransactionManager used for batch transactions
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new org.springframework.jdbc.datasource.DataSourceTransactionManager(dataSource);
    }
    
    /**
     * Override to provide custom batch configuration if needed.
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        // Use a custom datasource if needed
        this.dataSource = dataSource;
    }
}