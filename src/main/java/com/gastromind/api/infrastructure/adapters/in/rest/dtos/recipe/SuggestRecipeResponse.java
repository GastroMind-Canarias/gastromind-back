package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Receta sugerida por IA y referencia temporal en Redis para guardar como favorita")
/**
 * Representa suggest recipe response dentro del dominio de la aplicacion.
 */
public record SuggestRecipeResponse(
        @Schema(description = "Identificador para POST .../favorites/from-suggestion (TTL ~10 dAAas)")
        String suggestionId,
        @Schema(description = "Receta sugerida (aAAn no persistida en BD hasta guardar favorito)")
        RecipeResponse recipe
) {}






