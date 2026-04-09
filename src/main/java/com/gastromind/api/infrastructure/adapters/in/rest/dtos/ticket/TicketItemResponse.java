package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Línea de ticket devuelta por la API")
public record TicketItemResponse(
        @Schema(description = "ID de la línea")
        String id,

        @Schema(description = "ID del producto en catálogo")
        String product_id,

        @Schema(description = "Nombre del producto")
        String product_name,

        @Schema(description = "Del catálogo: conviene revisar el producto (p. ej. alta automática desde ticket)")
        boolean product_needs_review,

        @Schema(description = "Del catálogo: motivo de revisión del producto")
        String product_review_note,

        @Schema(description = "Cantidad comprada (en la unidad indicada por unit_id)")
        BigDecimal quantity,

        @Schema(description = "ID de la unidad de medida (tabla unit)")
        String unit_id,

        @Schema(description = "Nombre de la unidad (ej. Gramos, Unidades)")
        String unit_name,

        @Schema(description = "Código corto coherente con la unidad: g, kg, ml, l, ud")
        String unit_code,

        @Schema(description = "Precio por kg (g/kg), por litro (ml/l) o por unidad (ud), coherente con la unidad")
        BigDecimal price_unit,

        @Schema(description = "PENDING_REVIEW si la línea requiere confirmación; OK si está clara", example = "PENDING_REVIEW")
        String verification_status,

        @Schema(description = "Incidencias de la línea (ej. peso no legible, cantidad ambigua)")
        String line_note
) {
}
