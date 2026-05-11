package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

/**
 * Fusiona una tienda pendiente en el catálogo oficial enlazando ID existente o nombre nuevo.
 */
public record PendingStorePromoteRequest(
        String store_id,
        String store_name
) {
}
