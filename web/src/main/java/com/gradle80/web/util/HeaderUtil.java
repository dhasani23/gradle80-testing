package com.gradle80.web.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for handling HTTP headers.
 * 
 * This utility provides methods for creating standard HTTP headers for entity operations
 * such as creation, update, and deletion, ensuring consistent header responses across
 * the application for RESTful API operations.
 */
public final class HeaderUtil {

    private static final String X_ENTITY_ID = "X-entityId";
    private static final String X_STATUS = "X-status";
    private static final String ENTITY_CREATED = "Entity created with ID: ";
    private static final String ENTITY_UPDATED = "Entity updated with ID: ";
    private static final String ENTITY_DELETED = "Entity deleted with ID: ";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private HeaderUtil() {
        // Utility class, should not be instantiated
    }

    /**
     * Creates HTTP headers for entity creation responses.
     * 
     * Includes the entity ID and a status message about the creation.
     * 
     * @param entityId The ID of the created entity
     * @return HttpHeaders with entity creation information
     */
    public static HttpHeaders createEntityCreatedHeaders(Long entityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_ENTITY_ID, entityId.toString());
        headers.add(X_STATUS, ENTITY_CREATED + entityId);
        return headers;
    }

    /**
     * Creates HTTP headers for entity update responses.
     * 
     * Includes the entity ID and a status message about the update.
     * 
     * @param entityId The ID of the updated entity
     * @return HttpHeaders with entity update information
     */
    public static HttpHeaders createEntityUpdateHeaders(Long entityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_ENTITY_ID, entityId.toString());
        headers.add(X_STATUS, ENTITY_UPDATED + entityId);
        return headers;
    }

    /**
     * Creates HTTP headers for entity deletion responses.
     * 
     * Includes the entity ID and a status message about the deletion.
     * 
     * @param entityId The ID of the deleted entity
     * @return HttpHeaders with entity deletion information
     */
    public static HttpHeaders createEntityDeletionHeaders(Long entityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_ENTITY_ID, entityId.toString());
        headers.add(X_STATUS, ENTITY_DELETED + entityId);
        return headers;
    }
    
    // TODO: Consider adding methods for error headers with specific error codes and messages
    
    // FIXME: Add null checks for entityId parameter in all methods to prevent NullPointerException
}