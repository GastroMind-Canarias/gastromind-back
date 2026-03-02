package com.gastromind.api.infrastructure.adapters.in.rest.dtos.analysis;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sugerencia de compra habitual basada en el análisis del histórico de tickets")
public record FrequentPurchaseSuggestionResponse(

        @Schema(example = "prod-abc-123", description = "ID del producto") String product_id,

        @Schema(example = "Leche entera", description = "Nombre del producto") String product_name,

        @Schema(example = "5", description = "Número de tickets en los que apareció este producto") long frequency,

        @Schema(example = "2.0", description = "Cantidad media comprada por ticket") double avg_quantity,

        @Schema(example = "false", description = "Indica si el producto ya está registrado en compras habituales") boolean already_registered) {
}
