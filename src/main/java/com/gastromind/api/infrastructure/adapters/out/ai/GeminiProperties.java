package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.ai.gemini")
/**
 * Representa gemini dentro del dominio de la aplicacion.
 */
public class GeminiProperties {

    private String apiKey = "";

    private String model = "gemini-2.5-flash";

    private List<String> fallbackModels = new ArrayList<>();

    private String baseUrl = "https://generativelanguage.googleapis.com";
    /**
     * Devuelve api key.
     * @return valor actual.
     */

    public String getApiKey() {
        return apiKey;
    }
    /**
     * Define api key.
     * @param apiKey valor a utilizar.
     */

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    /**
     * Devuelve model.
     * @return valor actual.
     */

    public String getModel() {
        return model;
    }
    /**
     * Define model.
     * @param model valor a utilizar.
     */

    public void setModel(String model) {
        this.model = model;
    }
    /**
     * Devuelve fallback models.
     * @return lista actual.
     */

    public List<String> getFallbackModels() {
        return fallbackModels;
    }
    /**
     * Define fallback models.
     * @param fallbackModels valor a utilizar.
     */

    public void setFallbackModels(List<String> fallbackModels) {
        this.fallbackModels = fallbackModels != null ? fallbackModels : new ArrayList<>();
    }
    /**
     * Devuelve model attempt order.
     * @return lista actual.
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
    /**
     * Devuelve base url.
     * @return valor actual.
     */

    public String getBaseUrl() {
        return baseUrl;
    }
    /**
     * Define base url.
     * @param baseUrl valor a utilizar.
     */

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    /**
     * Indica si configured.
     * @return true si cumple la condicion; false en caso contrario.
     */

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}




