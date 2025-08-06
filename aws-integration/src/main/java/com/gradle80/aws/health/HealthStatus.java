package com.gradle80.aws.health;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AWS service health status model.
 * This class represents the health status of various AWS services
 * used within the application.
 */
public class HealthStatus {

    /**
     * Indicates if Simple Queue Service is healthy
     */
    private boolean sqsHealthy;

    /**
     * Indicates if Simple Notification Service is healthy
     */
    private boolean snsHealthy;

    /**
     * The timestamp when the health check was last performed
     */
    private LocalDateTime lastChecked;

    /**
     * Default constructor initializes with unhealthy state
     */
    public HealthStatus() {
        this.sqsHealthy = false;
        this.snsHealthy = false;
        this.lastChecked = LocalDateTime.now();
    }

    /**
     * Parameterized constructor
     *
     * @param sqsHealthy  health status of SQS
     * @param snsHealthy  health status of SNS
     * @param lastChecked timestamp of the last check
     */
    public HealthStatus(boolean sqsHealthy, boolean snsHealthy, LocalDateTime lastChecked) {
        this.sqsHealthy = sqsHealthy;
        this.snsHealthy = snsHealthy;
        this.lastChecked = lastChecked != null ? lastChecked : LocalDateTime.now();
    }

    /**
     * Determines if all AWS services are healthy
     *
     * @return true if all services are healthy, false otherwise
     */
    public boolean isOverallHealthy() {
        return sqsHealthy && snsHealthy;
    }

    /**
     * @return SQS health status
     */
    public boolean isSqsHealthy() {
        return sqsHealthy;
    }

    /**
     * @param sqsHealthy set SQS health status
     */
    public void setSqsHealthy(boolean sqsHealthy) {
        this.sqsHealthy = sqsHealthy;
        // TODO: Consider adding logging when health status changes
    }

    /**
     * @return SNS health status
     */
    public boolean isSnsHealthy() {
        return snsHealthy;
    }

    /**
     * @param snsHealthy set SNS health status
     */
    public void setSnsHealthy(boolean snsHealthy) {
        this.snsHealthy = snsHealthy;
        // TODO: Consider adding logging when health status changes
    }

    /**
     * @return the last check timestamp
     */
    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    /**
     * @param lastChecked set the last check timestamp
     */
    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    /**
     * Updates the last check timestamp to now
     */
    public void updateLastChecked() {
        this.lastChecked = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthStatus that = (HealthStatus) o;
        return sqsHealthy == that.sqsHealthy &&
                snsHealthy == that.snsHealthy &&
                Objects.equals(lastChecked, that.lastChecked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqsHealthy, snsHealthy, lastChecked);
    }

    @Override
    public String toString() {
        return "HealthStatus{" +
                "sqsHealthy=" + sqsHealthy +
                ", snsHealthy=" + snsHealthy +
                ", lastChecked=" + lastChecked +
                ", overallHealthy=" + isOverallHealthy() +
                '}';
    }
}