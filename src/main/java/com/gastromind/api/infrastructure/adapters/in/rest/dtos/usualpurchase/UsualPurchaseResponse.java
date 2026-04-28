package com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Respuesta de la configuraciAAn de compra habitual")
/**
 * Representa usual purchase response dentro del dominio de la aplicacion.
 */
public record UsualPurchaseResponse(

        @Schema(example = "pur-00123", description = "ID AAnico de la compra habitual")
        String id,

        @Schema(example = "usr-456-abc")
        String user_id,

        @Schema(example = "prod-789-xyz")
        String product_id,

        @Schema(example = "2.5")
        BigDecimal target_quantity
) {}






