package com.gastromind.api.infrastructure.adapters.in.rest.dtos.user;

import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "InformaciAAn pAAblica del usuario")
/**
 * Representa user response dentro del dominio de la aplicacion.
 */
public record UserResponse(

        @Schema(example = "usr-00123")
        String id,

        @Schema(example = "Juan PAArez")
        String name,

        @Schema(example = "juan@gastromind.com")
        String email,

        @Schema(example = "house-789-xyz")
        String houseHold_id,

        @Schema(example = "ROLE_MEMBER")
        Role role,

        @Schema(description = "Lista de alAArgenos asociados al usuario")
        List<AllergenResponse> allergens
) {}






