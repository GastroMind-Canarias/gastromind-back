package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

/**
 * Vista de cola de moderación: detecciones, conteo y resolución si procede.
 */
public record PendingStoreResponse(
        String id,
        String detected_name,
        int sightings_count,
        String status,
        String resolved_store_id,
        String rejection_reason
) {
}
