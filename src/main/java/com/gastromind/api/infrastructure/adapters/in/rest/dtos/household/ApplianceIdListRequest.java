package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Lista de ids de filas household_appliances")
/**
 * Representa appliance id list request dentro del dominio de la aplicacion.
 */
public record ApplianceIdListRequest(
        @NotEmpty @Schema(description = "UUIDs de registros de electrodomAAstico del hogar")
        List<String> ids
) {
}






