package com.gradle80.service.cache;

/**
 * Model class representing a cache entry.
 * This class holds the cached value along with its expiration time.
 * Used primarily by the InMemoryCacheManager to manage application caches.
 */
public class CacheEntry {

    /**
     * The actual value stored in the cache.
     */
    private final Object value;

    /**
     * Timestamp indicating when this cache entry expires.
     * Time is represented as milliseconds since the epoch.
     */
    private final long expiryTime;

    /**
     * Constructs a new cache entry with the specified value and expiry time.
     *
     * @param value      the value to be cached
     * @param expiryTime the timestamp when this entry expires
     */
    public CacheEntry(Object value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }

    /**
     * Returns the cached value.
     *
     * @return the cached value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the expiry time of this cache entry.
     *
     * @return the expiry time in milliseconds since the epoch
     */
    public long getExpiryTime() {
        return expiryTime;
    }

    /**
     * Determines whether this cache entry has expired.
     * An entry is considered expired if the current system time is greater than
     * the entry's expiry time.
     *
     * @return true if the entry has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    /**
     * Returns a string representation of this cache entry.
     *
     * @return a string representation including the value type and expiry time
     */
    @Override
    public String toString() {
        return "CacheEntry{" +
                "valueType=" + (value != null ? value.getClass().getSimpleName() : "null") +
                ", expiryTime=" + expiryTime +
                ", expired=" + isExpired() +
                '}';
    }

    // TODO: Consider adding a method to extend the lifetime of a cache entry
    
    // FIXME: Handle serialization for distributed cache scenarios
}