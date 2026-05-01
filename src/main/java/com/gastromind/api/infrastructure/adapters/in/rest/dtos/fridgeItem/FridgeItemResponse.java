package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Respuesta detallada de un producto en la nevera (el id de nevera no se repite: en /me es implicito; en rutas admin puede inferirse del path)")
/**
 * Representa fridge item response dentro del dominio de la aplicacion.
 */
public record FridgeItemResponse(
        @Schema(example = "uuid-item-12345")
        String id,

        @Schema(example = "1.50")
        BigDecimal quantity,

        @Schema(example = "2026-05-20")
        LocalDate expirationDate,

        @Schema(example = "OPENED")
        String status,

        @Schema(example = "Leche Entera")
        String productName
) {
}






