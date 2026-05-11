package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

/**
 * Aviso al cliente tras importar ticket: tienda aún en cola y acción esperada (promover, ignorar, etc.).
 */
public record PendingStoreInfoResponse(
        String pending_store_id,
        String detected_store_name,
        String action_required
) {
}
