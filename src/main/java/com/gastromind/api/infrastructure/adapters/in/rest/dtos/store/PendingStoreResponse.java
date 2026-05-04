package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

public record PendingStoreResponse(
        String id,
        String detected_name,
        int sightings_count,
        String status,
        String resolved_store_id,
        String rejection_reason
) {
}
