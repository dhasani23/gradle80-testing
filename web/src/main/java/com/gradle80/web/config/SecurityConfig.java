package com.gradle80.web.config;

import com.gradle80.api.service.AuthenticationService;
import com.gradle80.web.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the web module that sets up security rules,
 * authentication filters, and other security-related settings.
 * <p>
 * This class configures Spring Security with JWT-based authentication,
 * stateless session management, and defines access control rules for
 * various API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Authentication service used for token validation and user authentication.
     */
    private final AuthenticationService authenticationService;

    /**
     * Constructor for SecurityConfig.
     *
     * @param authenticationService the authentication service to use for token validation
     */
    @Autowired
    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Configures HTTP security settings including URL-based security,
     * CSRF protection, session management, and exception handling.
     *
     * @param http the HttpSecurity to configure
     * @throws Exception if configuration fails
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Disable CSRF as we're using stateless JWT authentication
            .csrf().disable()
            
            // Configure session management to be stateless (no sessions)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            
            // Define authorization rules
            .authorizeRequests()
                // Public endpoints that don't require authentication
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()
                .antMatchers("/health", "/info").permitAll()
                
                // Endpoints requiring specific permissions
                .antMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                
                // User endpoints - admin can access all, users can access their own
                .antMatchers("/api/users/**").hasRole("ADMIN")
                
                // Order endpoints require authentication
                .antMatchers("/api/orders/**").authenticated()
                
                // Any other request requires authentication
                .anyRequest().authenticated()
                .and()
            
            // Add custom JWT filter before the standard authentication filter
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
            
        // TODO: Configure proper authentication entry point and access denied handler
        // FIXME: Add exception translation for better error responses
    }

    /**
     * Creates and configures a JWT authentication filter bean.
     * This filter intercepts requests to validate JWT tokens and establish security context.
     *
     * @return the configured JWT authentication filter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authenticationService);
    }
}