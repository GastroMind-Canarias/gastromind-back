package com.gastromind.api.infrastructure.adapters.out.ai;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiRetryPolicyTest {

    @Test
    void shouldTryNextModel_onRateLimitAndOverloadStatuses() {
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(
                HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "429", null, null, null)));
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(
                HttpServerErrorException.create(HttpStatus.SERVICE_UNAVAILABLE, "503", null, null, null)));
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(
                HttpServerErrorException.create(HttpStatus.BAD_GATEWAY, "502", null, null, null)));
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(
                HttpServerErrorException.create(HttpStatus.GATEWAY_TIMEOUT, "504", null, null, null)));
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(
                HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "500", null, null, null)));
    }

    @Test
    void shouldTryNextModel_onResourceAccess() {
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(new ResourceAccessException("timeout")));
    }

    @Test
    void shouldNotTryNextModel_onClientErrorsOtherThan429() {
        assertFalse(GeminiRetryPolicy.shouldTryNextModel(
                HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400", null, null, null)));
        assertFalse(GeminiRetryPolicy.shouldTryNextModel(
                HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "401", null, null, null)));
    }

    @Test
    void shouldTryNextModel_whenCauseIsRetryable() {
        var inner = HttpServerErrorException.create(HttpStatus.SERVICE_UNAVAILABLE, "503", null, null, null);
        var wrapped = new RuntimeException(inner);
        assertTrue(GeminiRetryPolicy.shouldTryNextModel(wrapped));
    }
}
