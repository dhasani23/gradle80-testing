package com.gradle80.api.config;

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
 * Swagger configuration class for API documentation.
 * This class provides configuration for Swagger UI and API documentation.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Creates and configures a Docket bean for Swagger API documentation.
     * 
     * @return Configured Docket instance for API documentation
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gradle80"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    /**
     * Creates API information for Swagger documentation.
     * 
     * @return ApiInfo instance containing API metadata
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gradle80 API")
                .description("REST API documentation for the Gradle80 application")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .contact(new Contact("Gradle80 Team", "https://gradle80.com", "info@gradle80.com"))
                // TODO: Update contact information with actual project details
                .build();
    }
    
    // FIXME: Consider adding additional Swagger UI customization options
    // such as global response messages, security schemes, etc.
}