package com.gastromind.api.infrastructure.adapters.in.rest.dtos.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar una nueva categoria")
public record CategoryRequest(
        @Schema(example = "Lacteos")
        @NotBlank(message = "El nombre de la categoria es obligatorio")
        String name
) {
}
