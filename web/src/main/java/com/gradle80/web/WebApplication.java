package com.gradle80.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main web application entry point for the gradle80 application.
 * This class bootstraps the Spring Boot application and serves as the main entry point
 * for the web module.
 * 
 * The application uses component scanning to identify and register Spring components
 * across the various packages in the project.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.gradle80"})
@EntityScan(basePackages = {"com.gradle80.data.entity"})
@EnableJpaRepositories(basePackages = {"com.gradle80.data.repository"})
public class WebApplication {

    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Start the Spring application context
        SpringApplication.run(WebApplication.class, args);
        
        // Log application startup
        System.out.println("Gradle80 Web Application started successfully!");
        
        // TODO: Implement application startup event listeners for additional initialization tasks
        // FIXME: Configure proper logging instead of System.out
    }
    
    // Additional lifecycle methods can be added here as needed
    // For example:
    // @PostConstruct
    // public void init() {
    //     // Initialization code
    // }
}