package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.gemini")
public class GeminiProperties {

    /**
     * API key (env: GEMINI_API_KEY recomendado).
     */
    private String apiKey = "";

    /**
     * Nombre del modelo en la API REST (p. ej. gemini-2.5-flash).
     * El alias gemini-1.5-flash dejó de resolverse en v1beta; usa GEMINI_MODEL para cambiarlo.
     */
    private String model = "gemini-2.5-flash";

    private String baseUrl = "https://generativelanguage.googleapis.com";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
