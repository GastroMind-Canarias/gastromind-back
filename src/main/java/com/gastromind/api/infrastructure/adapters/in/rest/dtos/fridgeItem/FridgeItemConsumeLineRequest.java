package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Cada entrada del lote: id del item de nevera y cantidad a quitar (misma regla que el consume unitario).
 */
@Schema(description = "Una linea de consumo: que item y cuanto descontar")
public record FridgeItemConsumeLineRequest(
        @Schema(description = "ID del item de nevera", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotBlank(message = "El itemId es obligatorio")
        String itemId,

        @Schema(description = "Cantidad a descontar del stock de ese item", example = "0.5")
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity
) {
}
