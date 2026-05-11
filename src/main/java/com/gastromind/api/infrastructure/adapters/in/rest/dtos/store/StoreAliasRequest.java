package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo para registrar un sinónimo de tienda asociado al ID canónico actual.
 */
public record StoreAliasRequest(
        @NotBlank(message = "Alias obligatorio")
        String alias
) {
}
