package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Varias lineas de descuento a la vez; la API devuelve un resultado por linea en el mismo orden.
 */
@Schema(description = "Varios consumos parciales en una sola peticion (orden de respuesta = orden del listado)")
public record FridgeItemConsumeBatchRequest(
        @Schema(description = "Lineas de consumo (al menos una)")
        @NotEmpty(message = "Debes indicar al menos una linea de consumo")
        List<@Valid FridgeItemConsumeLineRequest> items
) {
}
