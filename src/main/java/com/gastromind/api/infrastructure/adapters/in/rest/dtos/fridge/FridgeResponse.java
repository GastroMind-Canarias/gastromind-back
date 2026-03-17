package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada de las neveras de la casa")
public record FridgeResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "6sd6v466-e29a-11d4-a526-8723716723561")
        String household_id
) {
}