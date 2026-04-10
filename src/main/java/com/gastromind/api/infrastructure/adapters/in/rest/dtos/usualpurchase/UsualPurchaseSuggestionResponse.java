package com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Sugerencia de producto habitual con stock en nevera y prioridad")
public record UsualPurchaseSuggestionResponse(

        @Schema(example = "prod-uuid")
        String product_id,

        @Schema(example = "Leche entera")
        String product_name,

        @Schema(description = "Cantidad objetivo (kg, l o ud según quantity_unit)")
        BigDecimal target_quantity,

        @Schema(example = "kg", allowableValues = {"kg", "l", "ud"})
        String quantity_unit,

        @Schema(description = "Cantidad agregada actual en la nevera del hogar")
        BigDecimal current_fridge_quantity,

        @Schema(description = "Puntuación (frecuencia × decaimiento por antigüedad)")
        double score,

        @Schema(description = "Número de tickets distintos con este producto en el periodo")
        int distinct_ticket_count,

        @Schema(description = "Fecha/hora de la última compra registrada en un ticket")
        LocalDateTime last_purchased_at,

        @Schema(description = "True si el stock está por debajo del umbral configurado respecto al target")
        boolean low_stock
) {}
