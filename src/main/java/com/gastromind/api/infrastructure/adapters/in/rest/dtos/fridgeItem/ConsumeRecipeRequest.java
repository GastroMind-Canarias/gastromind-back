package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Cuerpo del endpoint que descuenta inventario al marcar una receta como cocinada.
 * Se envia la lista de ingredientes ya resueltos (los mismos que muestra la sugerencia
 * o la receta guardada); el servidor decide a que items de nevera tocar y en que orden.
 */
@Schema(description = "Ingredientes consumidos al cocinar una receta. La operacion es todo o nada.")
public record ConsumeRecipeRequest(
        @Schema(description = "Ingredientes con cantidad usada. Al menos uno.")
        @NotEmpty(message = "Debes indicar al menos un ingrediente")
        List<@Valid IngredientUsageRequest> ingredientsUsed
) {
}
