package com.gradle80.service.cache;

/**
 * Interface for managing application caches.
 * 
 * This interface provides methods for storing, retrieving, and evicting
 * cache entries. Implementations should handle expiration of entries
 * based on the provided TTL (Time To Live) values.
 */
public interface CacheManager {
    
    /**
     * Retrieves an entry from the cache and casts it to the specified type.
     * 
     * @param cacheKey the unique key identifying the cached entry
     * @param clazz the expected class type of the cached value
     * @return the cached object, or null if not found or expired
     * @param <T> the type to cast the result to
     * @throws ClassCastException if the cached object cannot be cast to the requested type
     */
    <T> T get(String cacheKey, Class<T> clazz);
    
    /**
     * Stores an entry in the cache with a specified time-to-live.
     * 
     * @param cacheKey the unique key to identify this cache entry
     * @param value the value to be cached
     * @param ttlSeconds how long the entry should be valid in seconds
     */
    void put(String cacheKey, Object value, long ttlSeconds);
    
    /**
     * Removes an entry from the cache if it exists.
     * 
     * @param cacheKey the unique key identifying the cached entry to remove
     */
    void evict(String cacheKey);
}