package com.gradle80.data.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Configuration class for QueryDSL.
 * 
 * QueryDSL is a framework which enables the construction of type-safe SQL-like queries.
 * This configuration provides the necessary beans for using QueryDSL with JPA in the application.
 */
@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates and configures a JPAQueryFactory bean.
     * 
     * The JPAQueryFactory provides a fluent API for creating queries with QueryDSL.
     * It requires an EntityManager to execute the queries against the database.
     * 
     * @return configured JPAQueryFactory instance
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // Create a new JPAQueryFactory with the injected EntityManager
        return new JPAQueryFactory(entityManager);
    }

    // TODO: Add additional QueryDSL configuration methods if needed for advanced use cases
    
    // FIXME: Consider adding proper exception handling for QueryDSL query executions
}