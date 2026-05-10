package com.gastromind.api.domain.ports.out;

/**
 * Dedupe rápido en Redis cuando llega el mismo nombre de tienda normalizado varias veces seguidas.
 */
public interface PendingStoreCachePort {
    void rememberPendingSighting(String detectedNameNorm);
}
