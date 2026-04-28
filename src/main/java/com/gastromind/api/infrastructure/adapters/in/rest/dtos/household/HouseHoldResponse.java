package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta detallada del hogar")
/**
 * Representa house hold response dentro del dominio de la aplicacion.
 */
public record HouseHoldResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Hogar de Cesar")
        String name,
        @Schema(description = "Numero de miembros del hogar", example = "2")
        int membersCount,
        @Schema(description = "Lista de miembros del hogar")
        List<UserResponse> members,
        @Schema(example = "[\"HORNO\", \"AIR_FRYER\"]", description = "Lista de utensilios del hogar")
        List<Appliance> appliances
) {
}






