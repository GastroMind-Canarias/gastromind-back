package com.gastromind.api.infrastructure.adapters.out.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Ejecuta {@code :generateContent} contra Gemini, probando el modelo principal y los configurados
 * como fallback ante saturación del servicio, límites de tasa o fallos transitorios.
 */
@Component
public class GeminiGenerateContentClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiGenerateContentClient.class);

    private final GeminiProperties properties;
    private final RestClient restClient;

    public GeminiGenerateContentClient(GeminiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }

    /**
     * POST al endpoint generateContent; si la petición falla de forma recuperable y hay más modelos,
     * reintenta con el siguiente.
     *
     * @throws RestClientException si todos los intentos fallan o el error no es recuperable con otro modelo
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
