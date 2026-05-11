package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resultado de marcar una receta como cocinada: items que han cambiado en la nevera
 * y los ingredientes que se han dejado fuera del descuento (sin productId u otros casos).
 */
@Schema(description = "Resumen del descuento de inventario por cocinar una receta.")
public record ConsumeFromRecipeResponse(
        @Schema(description = "Items de nevera resultantes tras aplicar los descuentos. Si la cantidad llega a cero el item desaparece del inventario pero aparece aqui con cantidad 0 para que el cliente sepa que se gasto.")
        List<FridgeItemResponse> consumed,

        @Schema(description = "Lineas de la receta que no se han aplicado al inventario y motivo.")
        List<IgnoredIngredientResponse> ignored
) {
}
