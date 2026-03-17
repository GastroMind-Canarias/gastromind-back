package com.gastromind.api.infrastructure.adapters.in.rest.dtos.user;

import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Datos de entrada para la gestión de usuarios")
public record UserRequest(

        @Schema(example = "Juan Pérez")
        @NotBlank(message = "El name es obligatorio")
        String name,

        @Schema(example = "juan@gastromind.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @Schema(example = "Secret123!", description = "Mínimo 8 caracteres")
        @Size(min = 8, message = "La password debe tener al menos 8 caracteres")
        String password,

        @Schema(example = "house-789-xyz", description = "ID de la vivienda o grupo familiar")
        @NotBlank(message = "El houseHold_id es obligatorio")
        String houseHold_id,

        @Schema(example = "ROLE_MEMBER", allowableValues = {"ROLE_ADMIN", "ROLE_MEMBER", "ROLE_OWNER", "ROLE_PREMIUM_MEMBER"})
        @NotNull(message = "El role es obligatorio")
        Role role,

        @Schema(description = "Lista de alérgenos asociados al usuario")
        List<AllergenResponse> allergens
) {}
