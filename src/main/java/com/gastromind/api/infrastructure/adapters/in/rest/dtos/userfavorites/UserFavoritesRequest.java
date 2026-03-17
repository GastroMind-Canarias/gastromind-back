package com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para marcar una receta como favorita")
public record UserFavoritesRequest(

        @Schema(example = "usr-456-abc", description = "ID del usuario que marca el favorito")
        @NotBlank(message = "El user_id es obligatorio")
        String user_id,

        @Schema(example = "rec-789-xyz", description = "ID de la receta marcada como favorita")
        @NotBlank(message = "El recipe_id es obligatorio")
        String recipe_id
) {}
