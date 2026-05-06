package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Alta o edicion de item en la nevera del usuario (sin fridgeId)")
/**
 * Representa my fridge item request dentro del dominio de la aplicacion.
 */
public record MyFridgeItemRequest(
        @Schema(description = "ID del producto maestro", example = "550e8400-e29b-41d4-a716-446655440001")
        String productId,

        @Schema(description = "Nombre del producto para resolver o crear en catalogo", example = "Leche")
        String productName,

        @Schema(description = "Cantidad disponible (permite decimales)", example = "1.50")
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity,

        @Schema(description = "Fecha de caducidad estimada (opcional si el producto no caduca)", example = "2026-12-31")
        LocalDate expirationDate,

        @Schema(description = "Estado del item en nevera", example = "GOOD")
        @NotNull(message = "El estado es obligatorio")
        ItemStatus status
) {
}






