package com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Sugerencia de producto habitual con stock en nevera y prioridad")
/**
 * Representa usual purchase suggestion response dentro del dominio de la aplicacion.
 */
public record UsualPurchaseSuggestionResponse(

        @Schema(example = "prod-uuid")
        String product_id,

        @Schema(example = "Leche entera")
        String product_name,

        @Schema(description = "Cantidad objetivo (kg, l o ud segun quantity_unit)")
        BigDecimal target_quantity,

        @Schema(example = "kg", allowableValues = {"kg", "l", "ud"})
        String quantity_unit,

        @Schema(description = "Cantidad agregada actual en la nevera del hogar")
        BigDecimal current_fridge_quantity,

        @Schema(description = "Puntuacion (frecuencia Aa decaimiento por antiguedad)")
        double score,

        @Schema(description = "Numero de tickets distintos con este producto en el periodo")
        int distinct_ticket_count,

        @Schema(description = "Fecha/hora de la Ultima compra registrada en un ticket")
        LocalDateTime last_purchased_at,

        @Schema(description = "True si el stock esta por debajo del umbral configurado respecto al target")
        boolean low_stock
) {}






