package com.gastromind.api.domain.ports.out;

/**
 * Freno por usuario al crear alias de producto o tienda para no spamear catálogos (Redis u otro backend).
 */
public interface AliasRateLimitPort {
    boolean allowAliasCreation(String userId);
}
