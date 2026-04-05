package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Opcional. Comensales para la receta; si no se indica, se usa el número de miembros del hogar.")
public record SuggestRecipeRequest(
        @Schema(example = "6", description = "Incluye visitas u otros comensales respecto al censo del hogar")
        @Min(value = 1, message = "Las raciones deben ser al menos 1")
        Integer servings
) {}
