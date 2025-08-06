package com.gradle80.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Utility class providing logging functionality across the application.
 * Contains methods for creating loggers and measuring execution time.
 */
public class LoggingUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private LoggingUtils() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    /**
     * Get a SLF4J Logger for the specified class.
     *
     * @param clazz The class to get the logger for
     * @return The Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Execute a supplier function and log the execution time.
     * This is useful for performance monitoring of operations.
     *
     * @param <T>       The return type of the supplier
     * @param logger    The logger to use
     * @param operation Description of the operation being performed
     * @param supplier  The supplier function to execute
     * @return The result of the supplier execution
     */
    public static <T> T logExecutionTime(Logger logger, String operation, Supplier<T> supplier) {
        long startTime = System.currentTimeMillis();
        
        try {
            return supplier.get();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Log at different levels based on execution time
            if (duration > 5000) {
                logger.warn("Operation '{}' took {} ms to execute", operation, duration);
            } else if (duration > 1000) {
                logger.info("Operation '{}' took {} ms to execute", operation, duration);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Operation '{}' took {} ms to execute", operation, duration);
            }
        }
    }
    
    /**
     * Executes a runnable and logs the execution time.
     * Convenience method for void operations.
     *
     * @param logger    The logger to use
     * @param operation Description of the operation being performed
     * @param runnable  The runnable to execute
     */
    public static void logExecutionTime(Logger logger, String operation, Runnable runnable) {
        logExecutionTime(logger, operation, () -> {
            runnable.run();
            return null;
        });
    }
    
    /**
     * Creates a performance log message with consistent formatting.
     *
     * @param operation The operation name
     * @param duration  The duration in milliseconds
     * @return A formatted log message
     */
    private static String createPerformanceLogMessage(String operation, long duration) {
        return String.format("Performance: %s completed in %d ms", operation, duration);
    }
    
    // TODO: Add method to create a parameterized logger that includes correlation IDs
    
    // FIXME: Consider using AspectJ for more comprehensive performance monitoring
}