package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar una nueva nevera")
public record FridgeRequest(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "El identificador del hogar es obligatorio")
        String household_id
) {
}