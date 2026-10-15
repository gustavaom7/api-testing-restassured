package com.gustavaom7.helpers;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APIClient builder class for consistent API request configuration
 * Handles common headers, base URL, and logging across all tests
 */
public class APIClient {
    private static final Logger logger = LoggerFactory.getLogger(APIClient.class);

    public static final String USER_AGENT =
            "api-testing-restassured/1.0 (https://github.com/gustavaom7/api-testing-restassured; portfolio test suite)";

    private String baseURL;
    private long responseTimeout = 5000;
    private boolean enableLogging = true;

    public APIClient(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Build RequestSpecification with configured settings
     * @return RequestSpecification ready to use with RestAssured
     */
    public RequestSpecification build() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseURL)
                .setContentType("application/json")
                // Wikimedia asks API clients for a descriptive User-Agent with a contact URL;
                // generic or spoofed browser agents can be rate limited or blocked.
                .addHeader("User-Agent", USER_AGENT);

        // Attaches every request and response to the Allure report.
        builder.addFilter(new AllureRestAssured());

        if (enableLogging) {
            builder.addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter());
        }

        logger.info("API Client configured: baseURL={}, timeout={}ms", baseURL, responseTimeout);
        return builder.build();
    }

    /**
     * Set response timeout in milliseconds
     */
    public APIClient withTimeout(long timeoutMs) {
        this.responseTimeout = timeoutMs;
        return this;
    }

    /**
     * Enable/disable request-response logging
     */
    public APIClient withLogging(boolean enabled) {
        this.enableLogging = enabled;
        return this;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
