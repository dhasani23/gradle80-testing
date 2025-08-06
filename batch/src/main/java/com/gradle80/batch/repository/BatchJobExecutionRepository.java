package com.gradle80.batch.repository;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for batch job execution tracking.
 * 
 * This repository provides methods to store and retrieve batch job execution information.
 * It uses direct JDBC operations for performance and flexibility beyond what Spring Batch's
 * built-in repositories offer.
 */
@Repository
public class BatchJobExecutionRepository {

    /**
     * JDBC executor
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor injection of dependencies
     * 
     * @param jdbcTemplate the JDBC template for database operations
     */
    @Autowired
    public BatchJobExecutionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Save execution details to the custom tracking table.
     * This is in addition to Spring Batch's internal tracking.
     * 
     * @param jobExecution the job execution to save
     */
    public void saveJobExecution(JobExecution jobExecution) {
        // SQL for inserting job execution data
        String sql = "INSERT INTO CUSTOM_JOB_EXECUTION (" +
                "JOB_EXECUTION_ID, JOB_INSTANCE_ID, JOB_NAME, START_TIME, END_TIME, " +
                "STATUS, EXIT_CODE, EXIT_MESSAGE, LAST_UPDATED, CREATE_TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                jobExecution.getId(),
                jobExecution.getJobInstance().getId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime(),
                jobExecution.getStatus().toString(),
                jobExecution.getExitStatus().getExitCode(),
                jobExecution.getExitStatus().getExitDescription(),
                new Date(), // last updated time
                new Date() // create time
        );

        // Store job parameters as well
        saveJobParameters(jobExecution);
        
        // TODO: Add more detailed tracking like step executions if needed
    }

    /**
     * Save the job parameters for easier querying
     * 
     * @param jobExecution the job execution containing parameters
     */
    private void saveJobParameters(JobExecution jobExecution) {
        // FIXME: Handle potential null JobParameters
        if (jobExecution.getJobParameters() == null) {
            return;
        }

        JobParameters params = jobExecution.getJobParameters();
        for (Map.Entry<String, JobParameter> entry : params.getParameters().entrySet()) {
            String sql = "INSERT INTO CUSTOM_JOB_PARAMS (" +
                    "JOB_EXECUTION_ID, PARAM_KEY, PARAM_VALUE, PARAM_TYPE) " +
                    "VALUES (?, ?, ?, ?)";

            JobParameter param = entry.getValue();
            String paramType = getJobParameterType(param);
            String paramValue = param.getValue() != null ? param.getValue().toString() : null;

            jdbcTemplate.update(sql,
                    jobExecution.getId(),
                    entry.getKey(),
                    paramValue,
                    paramType
            );
        }
    }

    /**
     * Determine the type of a job parameter
     * 
     * @param parameter the job parameter
     * @return string representation of the parameter type
     */
    private String getJobParameterType(JobParameter parameter) {
        if (parameter.getValue() instanceof String) {
            return "STRING";
        } else if (parameter.getValue() instanceof Long) {
            return "LONG";
        } else if (parameter.getValue() instanceof Double) {
            return "DOUBLE";
        } else if (parameter.getValue() instanceof Date) {
            return "DATE";
        }
        return "STRING";
    }

    /**
     * Find executions for a specific job name.
     * 
     * @param jobName the name of the job
     * @return list of job executions for the job
     */
    public List<JobExecution> findJobExecutions(String jobName) {
        String sql = "SELECT e.JOB_EXECUTION_ID, e.JOB_INSTANCE_ID, e.JOB_NAME, " +
                "e.START_TIME, e.END_TIME, e.STATUS, e.EXIT_CODE, e.EXIT_MESSAGE " +
                "FROM CUSTOM_JOB_EXECUTION e " +
                "WHERE e.JOB_NAME = ? " +
                "ORDER BY e.START_TIME DESC";

        return jdbcTemplate.query(sql, new JobExecutionRowMapper(), jobName);
    }

    /**
     * RowMapper implementation for mapping database rows to JobExecution objects
     */
    private static class JobExecutionRowMapper implements RowMapper<JobExecution> {
        @Override
        public JobExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Create JobInstance
            Long instanceId = rs.getLong("JOB_INSTANCE_ID");
            String jobName = rs.getString("JOB_NAME");
            JobInstance jobInstance = new JobInstance(instanceId, jobName);

            // Create JobExecution with empty parameters
            Long executionId = rs.getLong("JOB_EXECUTION_ID");
            // Fix: Use the correct constructor that accepts JobParameters
            JobExecution jobExecution = new JobExecution(instanceId, new JobParameters());
            jobExecution.setJobInstance(jobInstance);

            // Set times
            jobExecution.setStartTime(getTimestamp(rs, "START_TIME"));
            jobExecution.setEndTime(getTimestamp(rs, "END_TIME"));

            // Set status
            String status = rs.getString("STATUS");
            if (status != null) {
                jobExecution.setStatus(BatchStatus.valueOf(status));
            }

            // Set exit status
            String exitCode = rs.getString("EXIT_CODE");
            String exitMessage = rs.getString("EXIT_MESSAGE");
            jobExecution.getExitStatus().addExitDescription(exitMessage);
            
            // Load parameters for this execution
            // TODO: Implement loading of parameters if needed
            
            return jobExecution;
        }
        
        private Date getTimestamp(ResultSet rs, String column) throws SQLException {
            Timestamp timestamp = rs.getTimestamp(column);
            return timestamp != null ? new Date(timestamp.getTime()) : null;
        }
    }
    
    /**
     * Retrieves job parameters for a specific job execution
     * 
     * @param jobExecutionId the job execution ID
     * @return the job parameters
     */
    public JobParameters getJobParameters(Long jobExecutionId) {
        String sql = "SELECT PARAM_KEY, PARAM_VALUE, PARAM_TYPE FROM CUSTOM_JOB_PARAMS " +
                "WHERE JOB_EXECUTION_ID = ?";
        
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, jobExecutionId);
        Map<String, JobParameter> parameterMap = new HashMap<>();
        
        for (Map<String, Object> row : rows) {
            String key = (String) row.get("PARAM_KEY");
            String value = (String) row.get("PARAM_VALUE");
            String type = (String) row.get("PARAM_TYPE");
            
            JobParameter parameter = createJobParameter(value, type);
            parameterMap.put(key, parameter);
        }
        
        return new JobParameters(parameterMap);
    }
    
    /**
     * Create a JobParameter based on type and value
     * 
     * @param value the parameter value as string
     * @param type the parameter type
     * @return the created JobParameter
     */
    private JobParameter createJobParameter(String value, String type) {
        if (value == null) {
            return new JobParameter((String) null);
        }
        
        switch (type) {
            case "LONG":
                return new JobParameter(Long.valueOf(value));
            case "DOUBLE":
                return new JobParameter(Double.valueOf(value));
            case "DATE":
                return new JobParameter(new Date(Long.parseLong(value)));
            default:
                return new JobParameter(value);
        }
    }
}