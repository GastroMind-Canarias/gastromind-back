package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Línea de inventario en nevera: cantidades, caducidad y, si aplica, un resumen del producto de catálogo (el id de nevera no se repite en rutas /me).")
public record FridgeItemResponse(
        @Schema(example = "uuid-item-12345")
        String id,

        @Schema(example = "1.50")
        BigDecimal quantity,

        @Schema(example = "2026-05-20")
        LocalDate expirationDate,

        @Schema(example = "OPENED")
        String status,

        @Schema(description = "Nombre mostrable (catálogo o etiqueta libre); se mantiene para clientes que no lean el bloque product.",
                example = "Leche Entera")
        String productName,

        @Schema(nullable = true)
        FridgeItemProductSummaryResponse product
) {
}






