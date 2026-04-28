package com.gastromind.api.infrastructure.adapters.out.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
/**
 * Representa gemini generate content client dentro del dominio de la aplicacion.
 */
public class GeminiGenerateContentClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiGenerateContentClient.class);

    private final GeminiProperties properties;
    private final RestClient restClient;
    /**
     * Constructor de gemini generate content client.
     * @param properties valor a utilizar.
     */

    public GeminiGenerateContentClient(GeminiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }
    /**
     * Realiza post generate content.
     * @param requestBody valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    public String postGenerateContent(String requestBody) {
        List<String> models = properties.getModelAttemptOrder();
        if (models.isEmpty()) {
            throw new IllegalStateException("No hay modelos Gemini configurados (app.ai.gemini.model)");
        }
        RestClientException lastRetryable = null;
        for (int i = 0; i < models.size(); i++) {
            String model = models.get(i);
            String url = buildUrl(model);
            try {
                return restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(String.class);
            } catch (RestClientException e) {
                boolean hasAnotherModel = i < models.size() - 1;
                if (hasAnotherModel && GeminiRetryPolicy.shouldTryNextModel(e)) {
                    log.warn("Gemini model '{}' failed ({}), trying fallback model", model, e.getMessage());
                    lastRetryable = e;
                    continue;
                }
                throw e;
            }
        }
        if (lastRetryable != null) {
            throw lastRetryable;
        }
        throw new IllegalStateException("Unexpected: no Gemini response and no exception");
    }

    private String buildUrl(String model) {
        String base = properties.getBaseUrl().replaceAll("/$", "");
        return UriComponentsBuilder
                .fromUriString(base + "/v1beta/models/" + model + ":generateContent")
                .queryParam("key", properties.getApiKey().trim())
                .build()
                .toUriString();
    }
}




