package com.gradle80.aws.error;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handler for AWS service errors.
 * Provides centralized error handling capabilities for AWS SNS and SQS operations.
 */
@Service
public class AwsErrorHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AwsErrorHandler.class);
    
    private static final int TOO_MANY_REQUESTS_STATUS_CODE = 429;
    private static final String THROTTLING_ERROR_CODE = "Throttling";
    private static final String THROTTLED_EXCEPTION_ERROR_CODE = "ThrottledException";
    private static final String LIMIT_EXCEEDED_ERROR_CODE = "LimitExceeded";
    
    /**
     * Handle SNS specific exceptions with appropriate logging and actions.
     * 
     * @param exception The AWS service exception from SNS operations
     */
    public void handleSnsException(AmazonServiceException exception) {
        logger.error("SNS Service Exception: {}, Status Code: {}, Error Code: {}", 
                exception.getMessage(), exception.getStatusCode(), exception.getErrorCode());
        
        // Log additional information for specific error cases
        if (isThrottlingError(exception)) {
            logger.warn("SNS throttling detected. Consider implementing backoff strategy.");
        } else if (exception.getStatusCode() >= 500) {
            logger.error("SNS service-side error detected. AWS Service may be experiencing issues.");
        } else if (exception.getStatusCode() == 403) {
            logger.error("Authentication/authorization error with SNS. Check IAM permissions.");
        }
        
        // TODO: Implement specific recovery mechanisms based on error types
        
        // Log request details that may be helpful for diagnostics
        logger.debug("SNS Request ID: {}", exception.getRequestId());
    }
    
    /**
     * Handle SQS specific exceptions with appropriate logging and actions.
     *
     * @param exception The AWS service exception from SQS operations
     */
    public void handleSqsException(AmazonServiceException exception) {
        logger.error("SQS Service Exception: {}, Status Code: {}, Error Code: {}", 
                exception.getMessage(), exception.getStatusCode(), exception.getErrorCode());
        
        // Handle specific SQS error scenarios
        if (isThrottlingError(exception)) {
            logger.warn("SQS throttling detected. Consider implementing backoff strategy.");
        } else if (exception.getErrorCode().contains("NonExistentQueue")) {
            logger.error("SQS operation attempted on non-existent queue. Check queue URL/name.");
        } else if (exception.getErrorCode().equals("OverLimit")) {
            logger.error("SQS queue limit exceeded. Check your queue quotas.");
        }
        
        // FIXME: We need a centralized retry mechanism for recoverable SQS errors
        
        // Log request details that may be helpful for diagnostics
        logger.debug("SQS Request ID: {}", exception.getRequestId());
    }
    
    /**
     * Handle general AWS client exceptions that are not specific to a service.
     * These are typically client-side issues (connectivity, credentials, etc.)
     *
     * @param exception The AWS client exception
     */
    public void handleClientException(AmazonClientException exception) {
        logger.error("AWS Client Exception: {}", exception.getMessage());
        
        // Determine if this is a connectivity issue
        if (exception.getMessage().contains("Unable to execute HTTP request") ||
            exception.getMessage().contains("Connection reset") ||
            exception.getMessage().contains("Connect timed out")) {
            
            logger.error("AWS connectivity issue detected. Check network connectivity.");
            // TODO: Implement automatic retry mechanism for connectivity issues
        }
        
        // Check for credential issues
        if (exception.getMessage().contains("The security token included in the request is invalid") ||
            exception.getMessage().contains("Access denied") ||
            exception.getMessage().contains("InvalidClientTokenId")) {
            
            logger.error("AWS credential issue detected. Check your AWS credentials.");
        }
        
        // Log the full stack trace at debug level
        logger.debug("Client exception details:", exception);
    }
    
    /**
     * Determines if an exception is retryable based on its type and properties.
     *
     * @param exception The exception to check
     * @return true if the exception is retryable, false otherwise
     */
    public boolean isRetryable(Exception exception) {
        if (exception instanceof AmazonServiceException) {
            AmazonServiceException ase = (AmazonServiceException) exception;
            
            // Check for throttling or service-side errors which are typically retryable
            if (isThrottlingError(ase) || ase.getStatusCode() >= 500) {
                return true;
            }
            
            // Specific error codes that are known to be retryable
            String errorCode = ase.getErrorCode();
            return errorCode.equals("ServiceUnavailable") ||
                   errorCode.equals("InternalFailure") ||
                   errorCode.equals("InternalError");
        } else if (exception instanceof AmazonClientException) {
            // Client exceptions like timeouts or connection issues are often retryable
            AmazonClientException ace = (AmazonClientException) exception;
            String message = ace.getMessage();
            
            return message.contains("Connection reset") ||
                   message.contains("Connect timed out") ||
                   message.contains("Read timed out") ||
                   message.contains("Unable to execute HTTP request");
        }
        
        // By default, assume not retryable for unknown exception types
        return false;
    }
    
    /**
     * Helper method to determine if an exception is related to throttling.
     *
     * @param exception The AWS service exception to check
     * @return true if the exception is throttling-related, false otherwise
     */
    private boolean isThrottlingError(AmazonServiceException exception) {
        return exception.getStatusCode() == TOO_MANY_REQUESTS_STATUS_CODE ||
               THROTTLING_ERROR_CODE.equals(exception.getErrorCode()) ||
               THROTTLED_EXCEPTION_ERROR_CODE.equals(exception.getErrorCode()) ||
               LIMIT_EXCEEDED_ERROR_CODE.equals(exception.getErrorCode());
    }
}