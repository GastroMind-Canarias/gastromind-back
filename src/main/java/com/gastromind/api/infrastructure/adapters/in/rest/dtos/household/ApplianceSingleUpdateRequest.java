package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cambiar el tipo de un registro de electrodoméstico")
public record ApplianceSingleUpdateRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Appliance appliance
) {
}
