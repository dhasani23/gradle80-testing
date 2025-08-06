package com.gradle80.test.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database utilities for integration tests.
 * Provides functionality to clean database and load test data.
 */
public class DatabaseTestHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestHelper.class);
    
    private final DataSource dataSource;
    
    /**
     * Constructor with required DataSource dependency.
     * 
     * @param dataSource the test database DataSource
     */
    public DatabaseTestHelper(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null");
        }
        this.dataSource = dataSource;
    }
    
    /**
     * Cleans the database by truncating all tables.
     * Tables with foreign key constraints are handled properly.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    public void cleanDatabase() throws SQLException {
        logger.info("Cleaning database...");
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement statement = connection.createStatement()) {
                // Disable foreign key checks to allow truncating tables with dependencies
                disableForeignKeyChecks(statement);
                
                // Get all tables
                List<String> tables = getAllTables(statement);
                
                // Truncate all tables
                for (String table : tables) {
                    logger.debug("Truncating table: {}", table);
                    statement.executeUpdate("TRUNCATE TABLE " + table);
                }
                
                // Re-enable foreign key checks
                enableForeignKeyChecks(statement);
                
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error while cleaning database", e);
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        
        logger.info("Database cleaned successfully");
    }
    
    /**
     * Loads test data from SQL script located at the specified path.
     * 
     * @param scriptPath path to the SQL script resource
     * @throws SQLException if there is an error executing the script
     */
    public void loadTestData(String scriptPath) throws SQLException {
        if (!StringUtils.hasText(scriptPath)) {
            throw new IllegalArgumentException("Script path must not be empty");
        }
        
        logger.info("Loading test data from script: {}", scriptPath);
        
        try (Connection connection = dataSource.getConnection()) {
            Resource resource = new ClassPathResource(scriptPath);
            
            if (!resource.exists()) {
                throw new IllegalArgumentException("Script not found: " + scriptPath);
            }
            
            // Execute the SQL script
            ScriptUtils.executeSqlScript(connection, resource);
            
            logger.info("Test data loaded successfully");
        } catch (SQLException e) {
            logger.error("Error executing SQL script: {}", scriptPath, e);
            throw e;
        }
    }
    
    /**
     * Gets a list of all tables in the database.
     * 
     * @param statement SQL statement to use
     * @return list of table names
     * @throws SQLException if there is an error querying the database
     */
    private List<String> getAllTables(Statement statement) throws SQLException {
        List<String> tables = new ArrayList<>();
        
        // This query works for most databases, but might need adjustments based on specific database
        // FIXME: Adapt this query for the specific database being used (MySQL, PostgreSQL, etc.)
        try (ResultSet rs = statement.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        
        return tables;
    }
    
    /**
     * Disables foreign key checks based on the database type.
     * 
     * @param statement SQL statement to use
     * @throws SQLException if there is an error executing the statement
     */
    private void disableForeignKeyChecks(Statement statement) throws SQLException {
        // TODO: Detect database type and use appropriate SQL
        // For MySQL:
        statement.execute("SET FOREIGN_KEY_CHECKS = 0");
        // For PostgreSQL:
        // No direct equivalent, would need to disable triggers or use deferrables
    }
    
    /**
     * Re-enables foreign key checks based on the database type.
     * 
     * @param statement SQL statement to use
     * @throws SQLException if there is an error executing the statement
     */
    private void enableForeignKeyChecks(Statement statement) throws SQLException {
        // TODO: Detect database type and use appropriate SQL
        // For MySQL:
        statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        // For PostgreSQL:
        // Re-enable triggers if disabled
    }
    
    /**
     * Executes a SQL file line by line.
     * Use this method for scripts that contain commands that must be executed individually.
     * 
     * @param scriptPath path to the SQL script resource
     * @throws SQLException if there is an error executing the SQL
     * @throws IOException if there is an error reading the file
     */
    public void executeScriptLineByLine(String scriptPath) throws SQLException, IOException {
        logger.info("Executing script line by line: {}", scriptPath);
        
        Resource resource = new ClassPathResource(scriptPath);
        try (
            Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))
        ) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                    continue;
                }
                
                sb.append(line);
                
                // Execute statement when we reach the end of it
                if (line.endsWith(";")) {
                    String sql = sb.toString();
                    try {
                        statement.execute(sql);
                    } catch (SQLException e) {
                        logger.error("Error executing SQL: {}", sql, e);
                        throw e;
                    }
                    sb.setLength(0); // Reset the string builder
                }
            }
            
            // Execute any remaining SQL
            if (sb.length() > 0) {
                statement.execute(sb.toString());
            }
        }
        
        logger.info("Script execution completed: {}", scriptPath);
    }
}