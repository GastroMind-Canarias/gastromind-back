package com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada de la unidad")
public record UnitResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Litros")
        String name
) {
}