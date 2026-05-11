package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

/**
 * Alias persistido mostrado tras crearlo o listarlo junto a la tienda.
 */
public record StoreAliasResponse(
        String id,
        String store_id,
        String alias
) {
}
