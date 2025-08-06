package com.gradle80.service.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In-memory implementation of the CacheManager interface.
 * 
 * This implementation uses a ConcurrentHashMap to store cache entries
 * and periodically cleans up expired entries to prevent memory leaks.
 * It is thread-safe and suitable for multi-threaded access.
 */
public class InMemoryCacheManager implements CacheManager {
    
    private static final Logger LOGGER = Logger.getLogger(InMemoryCacheManager.class.getName());
    
    /**
     * The internal cache storage using a thread-safe map implementation
     */
    private final Map<String, CacheEntry> cache;
    
    /**
     * Executor service for scheduling periodic cleanup tasks
     */
    private final ScheduledExecutorService cleanupExecutor;
    
    /**
     * Default cleanup interval in seconds
     */
    private static final int DEFAULT_CLEANUP_INTERVAL_SECONDS = 60;
    
    /**
     * Constructs a new InMemoryCacheManager with default settings.
     * Initializes the cache and schedules periodic cleanup of expired entries.
     */
    public InMemoryCacheManager() {
        this(DEFAULT_CLEANUP_INTERVAL_SECONDS);
    }
    
    /**
     * Constructs a new InMemoryCacheManager with a specified cleanup interval.
     * 
     * @param cleanupIntervalSeconds the interval at which expired entries should be cleaned up
     */
    public InMemoryCacheManager(int cleanupIntervalSeconds) {
        this.cache = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "cache-cleanup-thread");
            thread.setDaemon(true);
            return thread;
        });
        
        // Schedule periodic cleanup of expired entries
        this.cleanupExecutor.scheduleAtFixedRate(
                this::cleanExpiredEntries,
                cleanupIntervalSeconds,
                cleanupIntervalSeconds,
                TimeUnit.SECONDS
        );
        
        LOGGER.info("InMemoryCacheManager initialized with cleanup interval: " + cleanupIntervalSeconds + " seconds");
    }
    
    /**
     * Constructs a new InMemoryCacheManager with a specified cache map and cleanup interval.
     * This constructor is primarily for testing purposes.
     * 
     * @param cache the concurrent hash map to use for caching
     * @param cleanupIntervalMillis the cleanup interval in milliseconds
     */
    public InMemoryCacheManager(ConcurrentHashMap<Object, Object> cache, long cleanupIntervalMillis) {
        this.cache = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "cache-cleanup-thread");
            thread.setDaemon(true);
            return thread;
        });
        
        // Schedule periodic cleanup of expired entries
        long intervalSeconds = cleanupIntervalMillis / 1000;
        this.cleanupExecutor.scheduleAtFixedRate(
                this::cleanExpiredEntries,
                intervalSeconds,
                intervalSeconds,
                TimeUnit.SECONDS
        );
        
        LOGGER.info("InMemoryCacheManager initialized with custom cache and cleanup interval: " + intervalSeconds + " seconds");
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String cacheKey, Class<T> clazz) {
        if (cacheKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        
        CacheEntry entry = cache.get(cacheKey);
        
        // Return null if entry doesn't exist or is expired
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                // Clean up expired entry
                cache.remove(cacheKey);
                LOGGER.fine("Removed expired cache entry with key: " + cacheKey);
            }
            return null;
        }
        
        Object value = entry.getValue();
        
        // Return null if value is null
        if (value == null) {
            return null;
        }
        
        try {
            // Attempt to cast the value to the requested type
            return clazz.cast(value);
        } catch (ClassCastException e) {
            LOGGER.warning("Failed to cast cache value for key '" + cacheKey + 
                    "' to " + clazz.getName() + ". Actual type: " + 
                    value.getClass().getName());
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String cacheKey, Object value, long ttlSeconds) {
        if (cacheKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        
        if (ttlSeconds <= 0) {
            LOGGER.warning("Attempt to cache with non-positive TTL: " + ttlSeconds + 
                    " seconds for key: " + cacheKey);
            return;
        }
        
        // Calculate expiry time in milliseconds from now
        long expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        CacheEntry entry = new CacheEntry(value, expiryTime);
        
        cache.put(cacheKey, entry);
        LOGGER.fine("Added cache entry with key: " + cacheKey + 
                ", TTL: " + ttlSeconds + " seconds");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(String cacheKey) {
        if (cacheKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        
        CacheEntry removed = cache.remove(cacheKey);
        if (removed != null) {
            LOGGER.fine("Evicted cache entry with key: " + cacheKey);
        }
    }
    
    /**
     * Removes all expired entries from the cache.
     * This method is called periodically by the scheduled executor
     * and can also be called manually if needed.
     */
    public void cleanExpiredEntries() {
        int expiredCount = 0;
        try {
            long now = System.currentTimeMillis();
            
            // Use iterator to safely remove entries during iteration
            for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
                if (entry.getValue().isExpired()) {
                    cache.remove(entry.getKey());
                    expiredCount++;
                }
            }
            
            if (expiredCount > 0) {
                LOGGER.info("Cleaned up " + expiredCount + " expired cache entries. Current cache size: " + cache.size());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cleaning expired cache entries", e);
        }
    }
    
    /**
     * Returns the current size of the cache.
     * 
     * @return the number of entries currently in the cache
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Clears all entries from the cache regardless of expiration status.
     */
    public void clear() {
        cache.clear();
        LOGGER.info("Cache cleared");
    }
    
    /**
     * Properly shutdowns the cache manager, including any background tasks.
     * Should be called when the application is shutting down.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("InMemoryCacheManager shutdown completed");
    }
    
    // TODO: Add support for cache statistics (hit/miss rates, etc.)
    
    // FIXME: Consider implementing bulk operations for better performance
}