package com.gradle80.test.service;

import com.gradle80.api.service.OrderService;
import com.gradle80.api.service.UserService;
import com.gradle80.service.config.ServiceConfig;
import com.gradle80.test.config.TestConfig;
import com.gradle80.test.data.TestDataProvider;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for service integration tests.
 * Provides test-specific beans and imports required configurations.
 */
@Configuration
@Import({TestConfig.class, ServiceConfig.class})
public class ServiceTestConfig {
    
    /**
     * Optional custom configuration for service tests.
     * In a real application, you might want to provide specific configurations for testing.
     */
    
    // Example of providing a custom bean for testing:
    // @Bean
    // @Primary
    // public CustomService customTestService() {
    //     return new CustomTestServiceImpl();
    // }
}