package com.gradle80.test.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Utility class for HTTP mocking in integration tests.
 * Provides methods to set up mock endpoints and verify calls using WireMock.
 */
public class HttpMockUtils {
    
    private static WireMockServer wireMockServer;
    
    /**
     * Initializes the WireMock server with default configuration.
     * This should be called before tests that require HTTP mocking.
     * 
     * @param port The port on which the mock server should run
     */
    public static void initializeMockServer(int port) {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            wireMockServer = new WireMockServer(port);
            wireMockServer.start();
            WireMock.configureFor("localhost", port);
        }
    }
    
    /**
     * Stops the WireMock server if it's running.
     * This should be called after tests to clean up resources.
     */
    public static void stopMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
    
    /**
     * Sets up a mock endpoint that responds with the specified body.
     * 
     * @param path The URL path to mock (e.g., "/api/users")
     * @param responseBody The JSON or text response to return
     */
    public static void setupMockEndpoint(String path, String responseBody) {
        stubFor(any(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));
    }
    
    /**
     * Sets up a mock endpoint with custom status code and response body.
     * 
     * @param path The URL path to mock
     * @param statusCode The HTTP status code to return
     * @param responseBody The response body to return
     */
    public static void setupMockEndpoint(String path, int statusCode, String responseBody) {
        stubFor(any(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));
    }
    
    /**
     * Verifies that an endpoint was called the expected number of times.
     * 
     * @param path The URL path to verify
     * @param times The expected number of calls
     */
    public static void verifyEndpointCalled(String path, int times) {
        verify(exactly(times), anyRequestedFor(urlEqualTo(path)));
    }
    
    /**
     * Gets all requests made to a specific endpoint.
     * Useful for more complex verifications beyond simple count.
     * 
     * @param path The URL path to check
     * @return List of LoggedRequest objects containing details about each request
     */
    public static List<LoggedRequest> getRequestsForEndpoint(String path) {
        return findAll(anyRequestedFor(urlEqualTo(path)));
    }
    
    /**
     * Resets all WireMock mappings and request logs.
     * Useful between tests to ensure clean state.
     */
    public static void resetMockServer() {
        WireMock.reset();
    }
    
    /**
     * Sets up a delayed response for testing timeouts and async behavior.
     * 
     * @param path The URL path to mock
     * @param responseBody The response body to return
     * @param delayMillis The delay in milliseconds before responding
     */
    public static void setupDelayedMockEndpoint(String path, String responseBody, int delayMillis) {
        // FIXME: Add proper error handling for very large delay values
        stubFor(any(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)
                        .withFixedDelay(delayMillis)));
    }
    
    /**
     * Sets up a mock endpoint that returns different responses based on the number of calls.
     * Useful for testing retry logic.
     * 
     * @param path The URL path to mock
     * @param failureResponse The response to return for initial calls
     * @param successResponse The response to return after failures
     * @param failureCount How many times the endpoint should fail before succeeding
     * @param failureCode HTTP status code for failures
     * 
     * TODO: Implement more sophisticated scenario-based mocking for complex test cases
     */
    public static void setupFailThenSucceedEndpoint(String path, String failureResponse, 
                                                   String successResponse, int failureCount, 
                                                   int failureCode) {
        scenario("Fail then succeed scenario")
            .whenScenarioStateIs(com.github.tomakehurst.wiremock.client.WireMock.STARTED)
            .when(any(urlEqualTo(path)))
            .willReturn(aResponse()
                .withStatus(failureCode)
                .withBody(failureResponse))
            .willSetStateTo("Failure 1");

        for (int i = 1; i < failureCount; i++) {
            scenario("Fail then succeed scenario")
                .whenScenarioStateIs("Failure " + i)
                .when(any(urlEqualTo(path)))
                .willReturn(aResponse()
                    .withStatus(failureCode)
                    .withBody(failureResponse))
                .willSetStateTo("Failure " + (i + 1));
        }
        
        scenario("Fail then succeed scenario")
            .whenScenarioStateIs("Failure " + failureCount)
            .when(any(urlEqualTo(path)))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse));
    }
}