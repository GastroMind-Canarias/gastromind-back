package com.gastromind.api.infrastructure.adapters.out.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.suggestion-cache")
public class RecipeSuggestionCacheProperties {

    /** TTL en Redis para cada sugerencia (por defecto 10 días). */
    private int ttlDays = 10;

    private String keyPrefix = "gastromind:recipe:suggestion:";

    public int getTtlDays() {
        return ttlDays;
    }

    public void setTtlDays(int ttlDays) {
        this.ttlDays = ttlDays;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
