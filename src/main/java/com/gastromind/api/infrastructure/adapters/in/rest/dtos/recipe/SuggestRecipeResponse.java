package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Receta sugerida por IA y referencia temporal en Redis para guardar como favorita")
public record SuggestRecipeResponse(
        @Schema(description = "Identificador para POST .../favorites/from-suggestion (TTL ~10 días)")
        String suggestionId,
        @Schema(description = "Receta sugerida (aún no persistida en BD hasta guardar favorito)")
        RecipeResponse recipe
) {}
