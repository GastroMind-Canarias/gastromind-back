package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Lista de tipos de electrodomAAstico")
/**
 * Representa appliance type list request dentro del dominio de la aplicacion.
 */
public record ApplianceTypeListRequest(
        @Schema(description = "Tipos a aAAadir o a fijar como conjunto completo (PUT)")
        List<Appliance> appliances
) {
}






