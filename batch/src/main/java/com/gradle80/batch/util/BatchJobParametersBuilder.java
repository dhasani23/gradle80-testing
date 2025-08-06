package com.gradle80.batch.util;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * BatchJobParametersBuilder
 * 
 * A utility class for building Spring Batch job parameters with fluent API.
 * This class extends the functionality of Spring's JobParametersBuilder with 
 * additional convenience methods.
 */
public class BatchJobParametersBuilder {
    
    private JobParametersBuilder jobParametersBuilder;
    
    /**
     * Constructor initializes a new JobParametersBuilder.
     */
    public BatchJobParametersBuilder() {
        this.jobParametersBuilder = new JobParametersBuilder();
        // Add a unique run ID by default to ensure each job execution is unique
        this.addString("run.id", UUID.randomUUID().toString());
    }
    
    /**
     * Constructor initializes from existing job parameters.
     *
     * @param jobParameters the job parameters to initialize from
     */
    public BatchJobParametersBuilder(JobParameters jobParameters) {
        this.jobParametersBuilder = new JobParametersBuilder(jobParameters);
        // Add a unique run ID by default to ensure each job execution is unique
        this.addString("run.id", UUID.randomUUID().toString());
    }
    
    /**
     * Add a string parameter.
     *
     * @param key the parameter key
     * @param value the string value
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addString(String key, String value) {
        jobParametersBuilder.addString(key, value);
        return this;
    }
    
    /**
     * Add a string parameter with identifying flag.
     *
     * @param key the parameter key
     * @param value the string value
     * @param identifying whether the parameter is identifying
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addString(String key, String value, boolean identifying) {
        jobParametersBuilder.addString(key, value, identifying);
        return this;
    }
    
    /**
     * Add a date parameter.
     *
     * @param key the parameter key
     * @param value the date value
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addDate(String key, Date value) {
        jobParametersBuilder.addDate(key, value);
        return this;
    }
    
    /**
     * Add a date parameter with identifying flag.
     *
     * @param key the parameter key
     * @param value the date value
     * @param identifying whether the parameter is identifying
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addDate(String key, Date value, boolean identifying) {
        jobParametersBuilder.addDate(key, value, identifying);
        return this;
    }
    
    /**
     * Add a long parameter.
     *
     * @param key the parameter key
     * @param value the long value
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addLong(String key, Long value) {
        jobParametersBuilder.addLong(key, value);
        return this;
    }
    
    /**
     * Add a long parameter with identifying flag.
     *
     * @param key the parameter key
     * @param value the long value
     * @param identifying whether the parameter is identifying
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addLong(String key, Long value, boolean identifying) {
        jobParametersBuilder.addLong(key, value, identifying);
        return this;
    }
    
    /**
     * Add a double parameter.
     *
     * @param key the parameter key
     * @param value the double value
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addDouble(String key, Double value) {
        jobParametersBuilder.addDouble(key, value);
        return this;
    }
    
    /**
     * Add a double parameter with identifying flag.
     *
     * @param key the parameter key
     * @param value the double value
     * @param identifying whether the parameter is identifying
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addDouble(String key, Double value, boolean identifying) {
        jobParametersBuilder.addDouble(key, value, identifying);
        return this;
    }
    
    /**
     * Add all parameters from a JobParameters object.
     *
     * @param parameters the JobParameters to add
     * @return this builder instance for method chaining
     */
    public BatchJobParametersBuilder addParameters(JobParameters parameters) {
        if (parameters != null) {
            for (Map.Entry<String, JobParameter> entry : parameters.getParameters().entrySet()) {
                String key = entry.getKey();
                JobParameter param = entry.getValue();
                Object value = param.getValue();
                boolean identifying = param.isIdentifying();
                
                if (value instanceof String) {
                    addString(key, (String) value, identifying);
                } else if (value instanceof Long) {
                    addLong(key, (Long) value, identifying);
                } else if (value instanceof Double) {
                    addDouble(key, (Double) value, identifying);
                } else if (value instanceof Date) {
                    addDate(key, (Date) value, identifying);
                }
            }
        }
        return this;
    }
    
    /**
     * Convert the builder to JobParameters.
     *
     * @return the completed JobParameters object
     */
    public JobParameters toJobParameters() {
        return jobParametersBuilder.toJobParameters();
    }
}