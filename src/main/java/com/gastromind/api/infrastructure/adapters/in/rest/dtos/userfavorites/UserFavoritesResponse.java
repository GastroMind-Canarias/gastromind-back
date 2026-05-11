package com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites;

import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entrada de favorito en API: identifica la fila en favoritos y lleva la receta ya expandida,
 * mismo detalle que {@code GET /api/v1/recipes/{id}} (sin segunda llamada).
 */
@Schema(description = "Favorito con la receta completa anidada (id de receta en recipe.id)")
public record UserFavoritesResponse(

        @Schema(example = "fav-00123", description = "ID de la entrada en favoritos")
        String id,

        @Schema(example = "usr-456-abc")
        String user_id,

        @Schema(description = "Receta completa asociada al favorito")
        RecipeResponse recipe
) {
}
