package com.gastromind.api.infrastructure.adapters.in.rest.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada del producto")
/**
 * Representa product response dentro del dominio de la aplicacion.
 */
public record ProductResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Floppy pistacho")
        String name,
        @Schema(example = "true", allowableValues = {"true", "false"})
        boolean is_essential,
        @Schema(description = "True si conviene revisar nombre/categoria en catalogo")
        boolean needs_review,
        @Schema(description = "Detalle para el usuario (ej. alta automatica desde ticket)")
        String review_note,
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String allergen_id
) {
}






