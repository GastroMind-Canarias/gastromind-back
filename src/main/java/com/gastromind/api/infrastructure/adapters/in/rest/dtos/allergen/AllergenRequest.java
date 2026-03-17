package com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar un nuevo alergeno")
public record AllergenRequest(
        @Schema(example = "Nueces")
        @NotBlank(message = "El nombre del alergeno es obligatorio")
        String name
) {
}
