package com.gastromind.api.infrastructure.adapters.in.rest.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Objeto para registrar multiples productos por nombre")
public record ProductBatchRequest(
        @Schema(description = "Listado de nombres de producto", example = "[\"Leche\", \"Huevos\"]")
        @NotEmpty(message = "Debes indicar al menos un nombre de producto")
        List<String> names
) {
}
