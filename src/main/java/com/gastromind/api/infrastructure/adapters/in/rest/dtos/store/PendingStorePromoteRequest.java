package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

public record PendingStorePromoteRequest(
        String store_id,
        String store_name
) {
}
