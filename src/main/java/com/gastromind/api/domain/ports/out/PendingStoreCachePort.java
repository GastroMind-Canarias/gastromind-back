package com.gastromind.api.domain.ports.out;

public interface PendingStoreCachePort {
    void rememberPendingSighting(String detectedNameNorm);
}
