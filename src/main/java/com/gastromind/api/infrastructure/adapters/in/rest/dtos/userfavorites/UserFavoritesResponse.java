package com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de la relación de favorito")
public record UserFavoritesResponse(

        @Schema(example = "fav-00123", description = "ID único de la entrada en favoritos")
        String id,

        @Schema(example = "usr-456-abc")
        String user_id,

        @Schema(example = "rec-789-xyz")
        String recipe_id
) {}
