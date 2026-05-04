package com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Favorito con resumen de receta (detalle completo vía GET receta por recipe_id)")
/**
 * Representa user favorites response dentro del dominio de la aplicacion.
 */
public record UserFavoritesResponse(

        @Schema(example = "fav-00123", description = "ID de la entrada en favoritos")
        String id,

        @Schema(example = "usr-456-abc")
        String user_id,

        @Schema(example = "rec-789-xyz", description = "ID de la receta; usar para el detalle completo")
        String recipe_id,

        @Schema(example = "Huevos a la estampida")
        String title,

        @Schema(example = "60", description = "Tiempo de preparacion en minutos")
        int prep_time,

        @Schema(example = "HORNO", description = "Electrodomestico principal")
        Appliance appliance_needed
) {
}
