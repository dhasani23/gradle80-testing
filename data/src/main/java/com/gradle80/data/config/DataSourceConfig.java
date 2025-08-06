package com.gradle80.data.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database connection configuration for the data module.
 * 
 * This class provides beans for:
 * - DataSource configuration for connecting to the database
 * - JPA EntityManagerFactory configuration
 * - Transaction management support
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.gradle80.data.repository")
@PropertySource("classpath:application-data.properties")
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    /**
     * Creates and configures the DataSource based on properties in application-data.properties
     *
     * @return configured DataSource
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
        
        return dataSource;
    }

    /**
     * Creates and configures the EntityManagerFactory with Hibernate as the JPA provider
     *
     * @return configured LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("com.gradle80.data.entity");
        
        // Set Hibernate as the JPA provider
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        
        // Configure additional Hibernate properties
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", environment.getRequiredProperty("spring.jpa.properties.hibernate.dialect"));
        jpaProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql", "false"));
        jpaProperties.put("hibernate.format_sql", environment.getProperty("spring.jpa.properties.hibernate.format_sql", "false"));
        
        // Optional: configure second-level cache
        // TODO: Add cache configuration if needed for improved performance
        
        entityManagerFactory.setJpaProperties(jpaProperties);
        
        return entityManagerFactory;
    }

    /**
     * Creates and configures the transaction manager for JPA
     *
     * @return configured PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        
        // FIXME: If using multiple data sources, ensure proper transaction management is configured
        
        return transactionManager;
    }
}