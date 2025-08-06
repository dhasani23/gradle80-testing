package com.gradle80.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing Configuration.
 * 
 * This class configures JPA auditing for entity creation and modification tracking.
 * It provides an AuditorAware implementation to capture the current user as the auditor.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    /**
     * Creates an AuditorAware bean that provides the current authenticated username
     * for JPA auditing purposes. This is used to track entity creation and modification.
     * 
     * @return AuditorAware implementation that returns the current username or "system" if no user is authenticated
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                // FIXME: Consider a more appropriate default value or throw an exception
                return Optional.of("system");
            }
            
            // TODO: Enhance to handle different authentication types and extract principal information appropriately
            return Optional.of(authentication.getName());
        };
    }
}