package com.gradle80.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This exception is typically thrown when attempting to access or manipulate
 * a resource that does not exist in the system.
 */
public class ResourceNotFoundException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The name of the resource that was not found
     */
    private final String resourceName;
    
    /**
     * The name of the field used in the search
     */
    private final String fieldName;
    
    /**
     * The value of the field used in the search
     */
    private final Object fieldValue;
    
    /**
     * Constructs a new ResourceNotFoundException with detailed information about the resource
     * that could not be found.
     *
     * @param resourceName the name of the entity or resource type that was being searched for
     * @param fieldName the name of the field that was used as search criteria
     * @param fieldValue the value of the field that was used as search criteria
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super("RESOURCE_NOT_FOUND", String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Returns the name of the resource that was not found.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * Returns the name of the field used in the search.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Returns the value of the field used in the search.
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
    
    /**
     * Returns a string representation of this exception, including resource details.
     * 
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        return "ResourceNotFoundException [resourceName=" + resourceName + 
               ", fieldName=" + fieldName + 
               ", fieldValue=" + fieldValue + "] " + 
               super.toString();
    }
}