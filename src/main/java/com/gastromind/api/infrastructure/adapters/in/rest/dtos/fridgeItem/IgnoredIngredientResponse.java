package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ingrediente de la receta que el servidor no ha intentado descontar.
 * Pasa, por ejemplo, cuando llega sin {@code productId} (basicos, condimentos, etc.).
 * Devolverlo al cliente evita confusion: el item esta en la receta pero no toca el inventario.
 */
@Schema(description = "Ingrediente recibido en la peticion que no se ha descontado del inventario y por que.")
public record IgnoredIngredientResponse(
        @Schema(description = "Id del producto en catalogo si llego en la peticion.", nullable = true)
        String productId,

        @Schema(description = "Nombre del ingrediente tal como llego en la peticion.", nullable = true)
        String productName,

        @Schema(description = "Motivo por el que la linea no descuenta stock.", example = "Sin productId")
        String reason
) {
}
