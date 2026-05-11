package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Cada linea de ingrediente que entra cuando marcas una receta como cocinada.
 * Solo el {@code productId} permite emparejar con tus items de nevera; los ingredientes
 * que no traen id (por ejemplo "sal" o "agua") se ignoran sin fallar la peticion.
 */
@Schema(description = "Ingrediente de la receta con la cantidad a descontar del inventario del hogar")
public record IngredientUsageRequest(
        @Schema(description = "Id del producto en catalogo. Sin id no se descuenta nada y la linea se devuelve en ignored.",
                example = "550e8400-e29b-41d4-a716-446655440002",
                nullable = true)
        String productId,

        @Schema(description = "Nombre del ingrediente, util para mostrar en el resumen aunque no se use para el match.",
                example = "Tomate", nullable = true)
        String productName,

        @Schema(description = "Cantidad usada por la receta (la que se descontara del stock).", example = "0.75")
        @NotNull(message = "La cantidad usada es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad usada debe ser mayor a cero")
        BigDecimal quantityUsed
) {
}
