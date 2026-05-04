package com.gastromind.api.infrastructure.adapters.in.rest.handler;

import com.gastromind.api.domain.exceptions.AiRecipeException;
import com.gastromind.api.domain.exceptions.AiTicketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleAiRecipeException_returns429_whenCauseIsTooManyRequests() {
        var cause = HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "429", null, null, null);
        var ex = new AiRecipeException("cuota", cause);
        var res = handler.handleAiRecipeException(ex);
        assertEquals(429, res.getStatusCode().value());
    }

    @Test
    void handleAiRecipeException_returns503_whenNoRateLimitCause() {
        var ex = new AiRecipeException("proveedor caido");
        var res = handler.handleAiRecipeException(ex);
        assertEquals(503, res.getStatusCode().value());
    }

    @Test
    void handleAiTicketException_returns429_whenCauseChainHas429() {
        var cause = HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "429", null, null, null);
        var ex = new AiTicketException("limite", cause);
        var res = handler.handleAiTicketException(ex);
        assertEquals(429, res.getStatusCode().value());
    }

    @Test
    void handleAiTicketException_returns503_forOtherClientError() {
        var cause = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "400", null, null, null);
        var ex = new AiTicketException("mal request", cause);
        var res = handler.handleAiTicketException(ex);
        assertEquals(503, res.getStatusCode().value());
    }
}
