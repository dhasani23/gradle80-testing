package com.gradle80.web.model;

/**
 * Model class for rate limit tracking.
 * This class provides functionality to track request counts and automatically
 * reset when a specified time limit is reached.
 */
public class RateLimit {

    /**
     * The current request count
     */
    private int count;
    
    /**
     * Timestamp when the counter should be reset (in milliseconds)
     */
    private long resetTime;

    /**
     * Constructs a new rate limit object with default values.
     * Count is initialized to zero and resetTime to the current time.
     */
    public RateLimit() {
        this.count = 0;
        this.resetTime = System.currentTimeMillis();
    }

    /**
     * Constructs a new rate limit object with a specific reset timestamp.
     *
     * @param resetTime The timestamp when the counter should reset
     */
    public RateLimit(long resetTime) {
        this.count = 0;
        this.resetTime = resetTime;
    }

    /**
     * Increments the request count by one and returns the new count.
     *
     * @return The new request count after incrementing
     */
    public int incrementCount() {
        // FIXME: Consider thread safety concerns for concurrent access
        return ++count;
    }

    /**
     * Checks if the rate limit counter should be reset based on the current time.
     *
     * @return true if the current time is greater than or equal to resetTime, false otherwise
     */
    public boolean shouldReset() {
        return System.currentTimeMillis() >= resetTime;
    }

    /**
     * Resets the request count to zero.
     * This method should be called when the rate limit period expires.
     */
    public void reset() {
        // TODO: Consider updating resetTime to a new value based on configuration
        this.count = 0;
    }

    /**
     * Gets the current request count.
     *
     * @return The current request count
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the timestamp when the counter should be reset.
     *
     * @return The reset timestamp in milliseconds
     */
    public long getResetTime() {
        return resetTime;
    }

    /**
     * Sets the timestamp when the counter should be reset.
     *
     * @param resetTime The new reset timestamp in milliseconds
     */
    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }

    @Override
    public String toString() {
        return "RateLimit{" +
                "count=" + count +
                ", resetTime=" + resetTime +
                '}';
    }
}