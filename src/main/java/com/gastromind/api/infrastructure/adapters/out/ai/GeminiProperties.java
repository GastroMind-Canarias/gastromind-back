package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Modelos alternativos si el principal falla por saturación, 429, 5xx transitorios o errores de red.
     * Se prueban en orden; duplicados respecto al modelo principal se omiten.
     */
    private List<String> fallbackModels = new ArrayList<>();

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

    public List<String> getFallbackModels() {
        return fallbackModels;
    }

    public void setFallbackModels(List<String> fallbackModels) {
        this.fallbackModels = fallbackModels != null ? fallbackModels : new ArrayList<>();
    }

    /**
     * Modelo principal primero, luego fallbacks únicos (útil para reintentos con otro modelo).
     */
    public List<String> getModelAttemptOrder() {
        List<String> order = new ArrayList<>();
        if (model != null && !model.isBlank()) {
            order.add(model.trim());
        }
        for (String m : fallbackModels) {
            if (m != null && !m.isBlank()) {
                String t = m.trim();
                if (!order.contains(t)) {
                    order.add(t);
                }
            }
        }
        return order;
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
