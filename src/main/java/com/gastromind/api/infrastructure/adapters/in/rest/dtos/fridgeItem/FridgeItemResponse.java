package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Respuesta detallada de un producto en la nevera")
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
        String productName,

        @Schema(example = "uuid-fridge-000")
        String fridgeId
) {
}