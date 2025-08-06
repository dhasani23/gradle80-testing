package com.gradle80.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Security configuration for tests.
 * This class provides security settings specifically for testing environments.
 * It extends WebSecurityConfigurerAdapter to allow customization of security behaviors.
 */
@Configuration
@EnableWebSecurity
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configures HTTP security settings for testing.
     * This method disables certain security features that might interfere with testing
     * while still maintaining a reasonable security posture.
     * 
     * @param http The HttpSecurity object to configure
     * @throws Exception If configuration fails
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF for testing purposes
        http.csrf().disable()
            // Allow all requests to the H2 console if it exists
            .headers().frameOptions().sameOrigin().and()
            // Configure authorization rules
            .authorizeRequests()
                // Allow access to common test endpoints without authentication
                .antMatchers("/api/test/**", "/h2-console/**").permitAll()
                // Allow access to swagger UI for testing API documentation
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Other endpoints require authentication
                .anyRequest().authenticated()
            .and()
            // Use form login for testing with a custom login page
            .formLogin()
                .loginPage("/test-login")
                .permitAll()
            .and()
            // Allow HTTP basic authentication for API testing
            .httpBasic();
            
        // TODO: Add additional security configurations for specific test scenarios
    }

    /**
     * Configures authentication manager builder for test environment.
     * Sets up in-memory authentication with test users.
     * 
     * @param auth The AuthenticationManagerBuilder to configure
     * @throws Exception If configuration fails
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
            .passwordEncoder(passwordEncoder());
        
        // FIXME: Consider using a more dynamic user repository for complex test scenarios
    }

    /**
     * Creates an authentication manager bean for testing.
     * This exposes the authentication manager for use in test classes.
     * 
     * @return AuthenticationManager The configured authentication manager
     * @throws Exception If creating the authentication manager fails
     */
    @Bean
    @Override
    public AuthenticationManager testAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Creates a password encoder for test environments.
     * 
     * @return PasswordEncoder A BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    /**
     * Creates an in-memory user details service with test users.
     * 
     * @return UserDetailsService The configured user details service
     */
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // Create test users with different roles
        manager.createUser(User.withUsername("test-user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build());
            
        manager.createUser(User.withUsername("test-admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN", "USER")
            .build());
            
        return manager;
    }
}