package com.gradle80.test.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring configuration for test contexts.
 * This class provides configuration beans used during integration tests.
 */
@Configuration
public class TestConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);
    
    /**
     * Creates an in-memory test database for integration tests.
     * 
     * @return configured DataSource for tests
     */
    @Bean
    public DataSource testDataSource() {
        logger.info("Initializing test database");
        
        // Create an embedded in-memory database suitable for testing
        try {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb-" + System.currentTimeMillis())
                .addScript("classpath:schema-test.sql")
                .addScript("classpath:data-test.sql")
                .build();
        } catch (Exception e) {
            logger.error("Failed to initialize test database", e);
            // FIXME: Consider a more graceful fallback strategy if scripts are missing
            throw new RuntimeException("Could not initialize test database", e);
        }
    }
    
    /**
     * Configures ObjectMapper for JSON serialization in tests.
     * 
     * @return configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper testObjectMapper() {
        logger.debug("Creating test ObjectMapper");
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure common settings for testing
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // TODO: Add custom test modules or serializers if needed
        
        return mapper;
    }
}