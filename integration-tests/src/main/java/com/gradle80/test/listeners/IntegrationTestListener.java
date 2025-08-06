package com.gradle80.test.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IntegrationTestListener implements both TestNG's ITestListener and extends JUnit's TestWatcher
 * to provide consistent test execution logging and event handling across different test frameworks.
 * 
 * This listener can be used in both TestNG and JUnit test suites to capture test events
 * such as start, success, failure, skip and finish.
 */
public class IntegrationTestListener implements ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestListener.class);
    
    // ======== TestNG Listener Methods ========
    
    /**
     * Called when a test starts.
     * 
     * @param result the test result containing test information
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("Starting test: {}#{}", className, testName);
        
        // TODO: Add timing information and test context data collection
    }
    
    /**
     * Called when a test fails.
     * 
     * @param result the test result containing test information and failure details
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();
        
        logger.error("Test failed: {}#{}", className, testName);
        
        if (throwable != null) {
            logger.error("Failure reason: {}", throwable.getMessage(), throwable);
        }
        
        // FIXME: Implement screenshot capture for web tests on failure
    }
    
    /**
     * Called when a test passes.
     * 
     * @param result the test result containing test information
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("Test passed: {}#{}", className, testName);
    }
    
    /**
     * Called when a test is skipped.
     * 
     * @param result the test result containing test information
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("Test skipped: {}#{}", className, testName);
    }
    
    /**
     * Called when a test suite finishes execution.
     * 
     * @param context the test context containing test execution results
     */
    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test suite finished: {}", context.getName());
        logger.info("Total tests: {}, Passed: {}, Failed: {}, Skipped: {}",
                context.getAllTestMethods().length,
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }
    
    /**
     * Called when a test suite starts execution.
     * 
     * @param context the test context
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("Test suite started: {}", context.getName());
    }
    
    /**
     * Called when a test fails but is within success percentage.
     * 
     * @param result the test result containing test information
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // This method is called when a test fails but is within the success percentage
        logger.warn("Test failed but within success percentage: {}", 
                result.getMethod().getMethodName());
    }
    
    // ======== JUnit test handling methods ========
    
    /**
     * Handle JUnit test success
     */
    public void junitTestSucceeded(String testName) {
        logger.info("JUnit test succeeded: {}", testName);
    }
    
    /**
     * Handle JUnit test failure
     */
    public void junitTestFailed(String testName, Throwable e) {
        logger.error("JUnit test failed: {}", testName, e);
    }
    
    /**
     * Handle JUnit test starting
     */
    public void junitTestStarting(String testName) {
        logger.info("JUnit test starting: {}", testName);
    }
    
    /**
     * Handle JUnit test finished
     */
    public void junitTestFinished(String testName) {
        logger.info("JUnit test finished: {}", testName);
    }
    
    /**
     * Helper method to format test information consistently
     * 
     * @param className the test class name
     * @param methodName the test method name
     * @return formatted test identifier
     */
    private String formatTestIdentifier(String className, String methodName) {
        return String.format("%s#%s", className, methodName);
    }
}