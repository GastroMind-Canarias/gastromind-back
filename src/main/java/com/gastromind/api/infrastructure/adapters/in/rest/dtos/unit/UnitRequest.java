package com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar una unidad de medida")
/**
 * Representa unit request dentro del dominio de la aplicacion.
 */
public record UnitRequest(
        @Schema(example = "Gramos")
        @NotBlank(message = "El nombre de la unidad es obligatorio")
        String name
) {
}






