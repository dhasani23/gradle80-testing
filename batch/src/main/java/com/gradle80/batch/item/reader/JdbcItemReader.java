package com.gradle80.batch.item.reader;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Item reader implementation for database input in batch jobs.
 * This class provides a reusable way to configure JDBC-based readers for Spring Batch,
 * supporting SQL queries with custom row mapping.
 * 
 * @author gradle80
 * @param <T> The type of item being read from the database
 */
@Component
public class JdbcItemReader<T> {

    /**
     * The data source for database connections
     */
    private DataSource dataSource;
    
    /**
     * The SQL query to execute for retrieving data
     */
    private String sqlQuery;
    
    /**
     * The row mapper that converts each row to a domain object
     */
    private RowMapper<T> rowMapper;
    
    /**
     * Name of the query being executed (for identification in logs)
     */
    private String queryName;
    
    /**
     * Maximum number of rows to fetch at once
     */
    private int fetchSize = 100;
    
    /**
     * Default constructor
     */
    public JdbcItemReader() {
        // Default constructor
    }
    
    /**
     * Constructor with required dependencies
     * 
     * @param dataSource the database connection
     * @param sqlQuery the query to execute
     * @param rowMapper the mapper to convert rows to domain objects
     */
    @Autowired
    public JdbcItemReader(DataSource dataSource, String sqlQuery, RowMapper<T> rowMapper) {
        this.dataSource = dataSource;
        this.sqlQuery = sqlQuery;
        this.rowMapper = rowMapper;
    }
    
    /**
     * Creates and configures a JdbcCursorItemReader based on the current settings.
     * This method builds the reader with appropriate configuration for SQL execution,
     * row mapping, and connection handling.
     * 
     * @return a fully configured JdbcCursorItemReader ready for batch processing
     */
    public JdbcCursorItemReader<T> createReader() {
        // Validate required properties
        if (dataSource == null) {
            throw new IllegalStateException("DataSource must be provided");
        }
        
        if (sqlQuery == null || sqlQuery.trim().isEmpty()) {
            throw new IllegalStateException("SQL query must be provided");
        }
        
        if (rowMapper == null) {
            throw new IllegalStateException("RowMapper must be provided");
        }
        
        // Create the reader
        JdbcCursorItemReader<T> reader = new JdbcCursorItemReader<>();
        
        // Configure the reader
        reader.setDataSource(dataSource);
        reader.setSql(sqlQuery);
        reader.setRowMapper(rowMapper);
        reader.setFetchSize(fetchSize);
        
        // Set a name for the reader (useful for logging and metrics)
        if (queryName != null) {
            reader.setName(queryName);
        } else {
            reader.setName("JdbcItemReader");
        }
        
        // FIXME: Consider adding verification query or connection test
        
        return reader;
    }
    
    /**
     * Set the data source to use
     * 
     * @param dataSource the database connection
     * @return this instance for fluent API usage
     */
    public JdbcItemReader<T> withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }
    
    /**
     * Set the SQL query to execute
     * 
     * @param sqlQuery the SQL statement to retrieve data
     * @return this instance for fluent API usage
     */
    public JdbcItemReader<T> withSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }
    
    /**
     * Set the row mapper to use
     * 
     * @param rowMapper the mapper to convert database rows to domain objects
     * @return this instance for fluent API usage
     */
    public JdbcItemReader<T> withRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
        return this;
    }
    
    /**
     * Set a descriptive name for this reader
     * 
     * @param queryName name to identify this reader
     * @return this instance for fluent API usage
     */
    public JdbcItemReader<T> withQueryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    /**
     * Set the fetch size for the JDBC driver
     * 
     * @param fetchSize number of rows to fetch at once
     * @return this instance for fluent API usage
     */
    public JdbcItemReader<T> withFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }
    
    /**
     * Get the configured data source
     * 
     * @return the data source
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Get the configured SQL query
     * 
     * @return the SQL query
     */
    public String getSqlQuery() {
        return sqlQuery;
    }
    
    /**
     * Helper method to create a simple reader with just the basics
     * 
     * @param <T> the type to map to
     * @param dataSource the database connection
     * @param sqlQuery the SQL statement
     * @param rowMapper the row mapper
     * @return a configured JdbcCursorItemReader
     */
    public static <T> JdbcCursorItemReader<T> createSimpleReader(
            DataSource dataSource, String sqlQuery, RowMapper<T> rowMapper) {
        JdbcItemReader<T> builder = new JdbcItemReader<>();
        return builder
                .withDataSource(dataSource)
                .withSqlQuery(sqlQuery)
                .withRowMapper(rowMapper)
                .createReader();
    }
    
    /**
     * TODO: Add support for prepared statement parameters
     * This would allow for parameterized queries
     */
}