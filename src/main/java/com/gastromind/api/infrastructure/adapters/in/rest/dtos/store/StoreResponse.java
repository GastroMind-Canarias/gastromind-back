package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada del supermercado")
/**
 * Representa store response dentro del dominio de la aplicacion.
 */
public record StoreResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "LIDL")
        String name
) {
}






