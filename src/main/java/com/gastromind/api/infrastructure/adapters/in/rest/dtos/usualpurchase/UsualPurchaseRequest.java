package com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Datos para definir una compra habitual de un producto")
public record UsualPurchaseRequest(

        @Schema(example = "usr-456-abc", description = "ID del usuario")
        @NotBlank(message = "El user_id es obligatorio")
        String user_id,

        @Schema(example = "prod-789-xyz", description = "ID del producto")
        @NotBlank(message = "El product_id es obligatorio")
        String product_id,

        @Schema(example = "2.5", description = "Cantidad objetivo que se suele comprar")
        @NotNull(message = "La target_quantity es obligatoria")
        @Positive(message = "La target_quantity debe ser mayor que cero")
        BigDecimal target_quantity
) {}
