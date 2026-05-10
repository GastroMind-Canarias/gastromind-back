package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

/**
 * Descarta un candidato a tienda con motivo legible para auditoría.
 */
public record PendingStoreRejectRequest(
        String reason
) {
}
