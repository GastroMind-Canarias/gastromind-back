package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Alta masiva de líneas de inventario desde el cliente (validación por elemento del listado).
 */
@Schema(description = "Objeto para registrar multiples items en mi nevera")
public record MyFridgeItemBatchRequest(
        @Schema(description = "Listado de items a registrar en mi nevera")
        @NotEmpty(message = "Debes indicar al menos un item")
        List<@Valid MyFridgeItemRequest> items
) {
}
