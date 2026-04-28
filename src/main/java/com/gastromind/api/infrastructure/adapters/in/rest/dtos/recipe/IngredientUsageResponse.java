package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Uso de un producto del inventario en la receta")
/**
 * Representa ingredient usage response dentro del dominio de la aplicacion.
 */
public record IngredientUsageResponse(
        @Schema(description = "Id del producto en catAAlogo")
        String productId,
        @Schema(description = "Nombre del producto")
        String productName,
        @Schema(description = "Cantidad prevista para la receta (no superior al stock disponible al generar la sugerencia)")
        BigDecimal quantityUsed,
        @Schema(description = "Stock disponible en el hogar al generar la sugerencia (referencia)")
        BigDecimal quantityAvailable
) {
}






