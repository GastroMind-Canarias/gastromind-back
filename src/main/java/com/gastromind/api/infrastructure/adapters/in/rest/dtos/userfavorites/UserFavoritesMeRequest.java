package com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Marcar receta como favorita para el usuario autenticado (sin user_id en cuerpo)")
/**
 * Representa user favorites me request dentro del dominio de la aplicacion.
 */
public record UserFavoritesMeRequest(

        @Schema(example = "rec-789-xyz", description = "ID de la receta marcada como favorita")
        @NotBlank(message = "El recipe_id es obligatorio")
        String recipe_id
) {}






