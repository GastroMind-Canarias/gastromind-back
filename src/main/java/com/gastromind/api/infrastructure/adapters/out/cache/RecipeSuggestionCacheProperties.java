package com.gastromind.api.infrastructure.adapters.out.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.suggestion-cache")
/**
 * Representa recipe suggestion cache dentro del dominio de la aplicacion.
 */
public class RecipeSuggestionCacheProperties {

    private int ttlDays = 10;

    private String keyPrefix = "gastromind:recipe:suggestion:";
    /**
     * Devuelve ttl days.
     * @return valor configurado.
     */

    public int getTtlDays() {
        return ttlDays;
    }
    /**
     * Define ttl days.
     * @param ttlDays valor a utilizar.
     */

    public void setTtlDays(int ttlDays) {
        this.ttlDays = ttlDays;
    }
    /**
     * Devuelve key prefix.
     * @return valor actual.
     */

    public String getKeyPrefix() {
        return keyPrefix;
    }
    /**
     * Define key prefix.
     * @param keyPrefix valor a utilizar.
     */

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}




