package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Línea de ticket asociada a un producto del catálogo")
public record TicketItemRequest(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String product_id,

        @Schema(description = "Nombre libre del producto cuando no existe en catálogo", example = "Pan chapata")
        String line_product_name,

        @Schema(example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        BigDecimal quantity,

        @Schema(example = "1.45")
        @NotNull(message = "El precio unitario es obligatorio")
        @Positive(message = "El precio unitario debe ser mayor que cero")
        BigDecimal price_unit,

        @Schema(description = "ID de unidad (tabla unit). Si se omite, se usa Unidades.", example = "uuid-de-gramos")
        String unit_id,

        @Schema(description = "Opcional: PENDING_REVIEW | OK. Por defecto OK.", example = "OK")
        String verification_status,

        @Schema(description = "Opcional: nota de incidencias en la línea")
        String line_note
) {
}
