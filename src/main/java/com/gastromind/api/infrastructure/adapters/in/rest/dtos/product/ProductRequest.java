package com.gastromind.api.infrastructure.adapters.in.rest.dtos.product;

import com.gastromind.api.domain.models.Allergen;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Objeto para registrar un nuevo producto")
public record ProductRequest(
        @Schema(example = "Floppy pistacho")
        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,
        @Schema(example = "true", allowableValues = {"true", "false"})
        @NotNull(message = "Debe tener alguno de los dos valores")
        boolean is_essential,
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String allergen_id
) {
}
