package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;

/**
 * Resultado de intentar casar el texto del ticket con una tienda oficial o una entrada pendiente.
 */
public record StoreResolutionResult(Store store, PendingStore pendingStore, String detectedStoreName) {
    public boolean resolved() {
        return store != null;
    }
}
