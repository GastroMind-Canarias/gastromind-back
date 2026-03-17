package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para registrar un nuevo supermercado")
public record StoreRequest(
        @Schema(example = "Mercadona")
        @NotBlank(message = "El nombre del supermercado es obligatorio")
        String name
) {
}