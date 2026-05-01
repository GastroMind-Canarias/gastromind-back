package com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada de los alergenos")
/**
 * Representa allergen response dentro del dominio de la aplicacion.
 */
public record AllergenResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Lactosa")
        String name
) {
}






