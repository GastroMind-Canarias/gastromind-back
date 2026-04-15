package com.gastromind.api.infrastructure.adapters.out.ai;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeminiGenerateContentClientTest {

    @Test
    void postGenerateContent_shouldFailWhenNoModelsConfigured() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        props.setModel(" ");
        props.setFallbackModels(List.of());
        GeminiGenerateContentClient client = new GeminiGenerateContentClient(props);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> client.postGenerateContent("{}"));
        assertEquals("No hay modelos Gemini configurados (app.ai.gemini.model)", ex.getMessage());
    }

    @Test
    void postGenerateContent_shouldUseFallbackModelOnRetryableFailure() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        props.setModel("m1");
        props.setFallbackModels(List.of("m2"));
        props.setBaseUrl("https://example.com/");
        GeminiGenerateContentClient client = new GeminiGenerateContentClient(props);

        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        ReflectionTestUtils.setField(client, "restClient", restClient);

        RestClientException retryable = HttpServerErrorException.create(
                HttpStatusCode.valueOf(503), "unavailable", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);
        when(restClient.post().uri(anyString()).contentType(any()).body(anyString()).retrieve().body(String.class))
                .thenThrow(retryable)
                .thenReturn("{\"ok\":true}");

        String out = client.postGenerateContent("{\"x\":1}");
        assertEquals("{\"ok\":true}", out);
    }

    @Test
    void postGenerateContent_shouldThrowImmediatelyOnNonRetryableFailure() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        props.setModel("m1");
        props.setFallbackModels(List.of("m2"));
        GeminiGenerateContentClient client = new GeminiGenerateContentClient(props);

        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        ReflectionTestUtils.setField(client, "restClient", restClient);

        RestClientException nonRetryable = HttpServerErrorException.create(
                HttpStatusCode.valueOf(400), "bad request", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);
        when(restClient.post().uri(anyString()).contentType(any()).body(anyString()).retrieve().body(String.class))
                .thenThrow(nonRetryable);

        RestClientException ex = assertThrows(RestClientException.class, () -> client.postGenerateContent("{}"));
        assertEquals(nonRetryable, ex);
    }
}
