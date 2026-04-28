package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar un nuevo hogar")
/**
 * Representa house hold request dentro del dominio de la aplicacion.
 */
public record HouseHoldRequest(
        @Schema(example = "Hogar de Paolo")
        @NotBlank(message = "El nombre del hogar de la unidad familiar")
        String name
) {
}






