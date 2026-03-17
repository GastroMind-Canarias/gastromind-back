package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Objeto para registrar o actualizar un producto dentro de la nevera")
public record FridgeItemRequest(
        @Schema(description = "ID del producto maestro", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotBlank(message = "El identificador del producto es obligatorio")
        String productId,

        @Schema(description = "ID de la nevera donde se almacena", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "El identificador de la nevera es obligatorio")
        String fridgeId,

        @Schema(description = "Cantidad disponible (permite decimales)", example = "1.50")
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity,

        @Schema(description = "Fecha de caducidad estimada", example = "2026-12-31")
        LocalDate expirationDate,

        @Schema(description = "Estado del producto", example = "GOOD", allowableValues = {"GOOD", "OPENED", "EXPIRED"})
        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}