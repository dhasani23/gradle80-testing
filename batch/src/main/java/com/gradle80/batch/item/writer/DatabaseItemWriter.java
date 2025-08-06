package com.gradle80.batch.item.writer;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Item writer for database output.
 * 
 * Provides functionality to write batch items to a database using JDBC.
 * This writer is designed to efficiently handle bulk inserts using Spring Batch's
 * JdbcBatchItemWriter implementation.
 */
@Component
public class DatabaseItemWriter<T> implements ItemWriter<T> {

    /**
     * The data source for database connections
     */
    private final DataSource dataSource;
    
    /**
     * The SQL insert query to be executed for each item
     */
    private final String sqlQuery;
    
    /**
     * The delegated JdbcBatchItemWriter
     */
    private JdbcBatchItemWriter<T> itemWriter;

    /**
     * Constructor for dependency injection
     * 
     * @param dataSource The database connection source
     * @param sqlQuery The SQL insert query
     */
    @Autowired
    public DatabaseItemWriter(DataSource dataSource, String sqlQuery) {
        this.dataSource = dataSource;
        this.sqlQuery = sqlQuery;
        this.itemWriter = createWriter();
    }
    
    /**
     * Creates and configures a JdbcBatchItemWriter for database operations.
     * The writer is configured with the provided SQL query and data source.
     * 
     * @return A configured JdbcBatchItemWriter instance
     */
    private JdbcBatchItemWriter<T> createWriter() {
        // FIXME: Add parameter source factory based on actual domain objects
        
        return new JdbcBatchItemWriterBuilder<T>()
                .dataSource(dataSource)
                .sql(sqlQuery)
                .assertUpdates(true) // Will throw an exception if no update is done
                // TODO: Configure the appropriate item prepared statement setter if needed
                // TODO: Add specific batch size if different from default
                .build();
    }
    
    /**
     * Write all items.
     * 
     * @param items the items to write
     * @throws Exception if error occurs during write
     */
    @Override
    public void write(List<? extends T> items) throws Exception {
        itemWriter.write(items);
    }
}