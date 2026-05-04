package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

public record PendingStoreInfoResponse(
        String pending_store_id,
        String detected_store_name,
        String action_required
) {
}
