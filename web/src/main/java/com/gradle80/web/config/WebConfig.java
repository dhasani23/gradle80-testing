package com.gradle80.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gradle80.web.filter.ResponseEnhancer;
import com.gradle80.web.filter.StandardResponseEnhancer;

/**
 * Web configuration class for the Gradle80 web module.
 * 
 * This class provides configuration for web-specific features like CORS settings
 * and registers beans required for web request/response handling.
 * 
 * @author Gradle80 Web Team
 * @version 1.0
 */
@Configuration
public class WebConfig {

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the application.
     * 
     * Allows specified origins, methods and headers to access the API.
     * 
     * @return a configured WebMvcConfigurer with CORS settings
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "https://gradle80.com")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                    .exposedHeaders("Authorization")
                    .allowCredentials(true)
                    .maxAge(3600);
                
                // TODO: Update allowed origins for different environments (dev, test, prod)
                // FIXME: Review security implications of CORS configuration before production deployment
            }
        };
    }
    
    /**
     * Creates and registers the response enhancer bean.
     * 
     * The response enhancer is responsible for adding standard headers and formatting
     * to all API responses before they are sent to clients.
     * 
     * @return ResponseEnhancer implementation to use for enhancing responses
     */
    @Bean
    public ResponseEnhancer responseEnhancer() {
        return new StandardResponseEnhancer();
        
        // TODO: Consider environment-specific response enhancers with different behaviors
    }
}