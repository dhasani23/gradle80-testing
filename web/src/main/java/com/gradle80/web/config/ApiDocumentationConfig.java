package com.gradle80.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class for Swagger API documentation.
 * 
 * This class sets up the Swagger documentation for the application's REST API,
 * including API information, base package scanning, and path patterns to include.
 */
@Configuration
@EnableSwagger2
public class ApiDocumentationConfig {
    
    /**
     * Configures the Swagger Docket bean for API documentation.
     * 
     * @return Docket object configured for the application
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gradle80.web.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
    
    /**
     * Provides API information for Swagger documentation.
     * 
     * @return ApiInfo object with application details
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gradle80 REST API")
                .description("API documentation for Gradle80 application")
                .version("1.0.0")
                .contact(new Contact("Gradle80 Team", "https://gradle80.com", "support@gradle80.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }
    
    // TODO: Add security scheme configuration for JWT authentication
    
    // FIXME: Update API info with actual project details when available
}