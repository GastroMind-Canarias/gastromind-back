package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada del hogar")
public record HouseHoldResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Hogar de Cesar")
        String name,
        @Schema(example = "2")
        int members
) {
}
