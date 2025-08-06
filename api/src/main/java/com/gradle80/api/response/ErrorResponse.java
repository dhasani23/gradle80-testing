package com.gradle80.api.response;

import com.gradle80.api.model.ApiResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Error response model that extends the base ApiResponse class.
 * This class is used to provide detailed error information when API requests fail.
 */
public class ErrorResponse extends ApiResponse {
    
    /**
     * A standardized error code to identify the type of error.
     * Could be mapped to specific HTTP status codes or application-specific error codes.
     */
    private String errorCode;
    
    /**
     * List of detailed error messages providing additional context about the error.
     * Useful for validation errors where multiple issues might need to be reported.
     */
    private List<String> details;
    
    /**
     * Default constructor.
     * Initializes an error response with default values.
     */
    public ErrorResponse() {
        super(false, "An error occurred");
        this.details = new ArrayList<>();
    }
    
    /**
     * Constructs a new error response with the given parameters.
     *
     * @param message   error message
     * @param errorCode error code
     */
    public ErrorResponse(String message, String errorCode) {
        super(false, message);
        this.errorCode = errorCode;
        this.details = new ArrayList<>();
    }
    
    /**
     * Constructs a new error response with the given parameters.
     *
     * @param message   error message
     * @param errorCode error code
     * @param details   list of error details
     */
    public ErrorResponse(String message, String errorCode, List<String> details) {
        super(false, message);
        this.errorCode = errorCode;
        this.details = details != null ? details : new ArrayList<>();
    }
    
    /**
     * Factory method to create a standard validation error response.
     *
     * @param details list of validation error messages
     * @return a new ErrorResponse instance with appropriate code and message
     */
    public static ErrorResponse validationError(List<String> details) {
        return new ErrorResponse("Validation failed", "VALIDATION_ERROR", details);
    }
    
    /**
     * Factory method to create a standard not found error response.
     *
     * @param resourceType the type of resource that wasn't found
     * @param id the identifier that was used to look up the resource
     * @return a new ErrorResponse instance with appropriate code and message
     */
    public static ErrorResponse notFound(String resourceType, Object id) {
        List<String> details = new ArrayList<>();
        details.add(resourceType + " with id " + id + " not found");
        return new ErrorResponse(resourceType + " not found", "NOT_FOUND", details);
    }
    
    /**
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * @param errorCode the error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * @return the list of error details
     */
    public List<String> getDetails() {
        return details;
    }
    
    /**
     * @param details the list of error details to set
     */
    public void setDetails(List<String> details) {
        this.details = details != null ? details : new ArrayList<>();
    }
    
    /**
     * Adds a single detail message to the error details list.
     *
     * @param detail the error detail to add
     * @return this ErrorResponse instance for method chaining
     */
    public ErrorResponse addDetail(String detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
        return this;
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "success=" + isSuccess() +
                ", message='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", errorCode='" + errorCode + '\'' +
                ", details=" + details +
                '}';
    }
    
    // TODO: Consider implementing equals() and hashCode() methods for proper comparison
    
    /**
     * FIXME: The error code should be standardized across the application
     * and possibly mapped to appropriate HTTP status codes when used in REST controllers.
     */
}