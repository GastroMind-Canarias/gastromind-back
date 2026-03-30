package com.gastromind.api.infrastructure.security.auth.dtos;

import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Datos necesarios para registrar un nuevo usuario en el sistema")
public record RegisterRequest(

        @Schema(example = "juan_gastro", description = "Nombre de usuario único")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres")
        String username,

        @Schema(example = "Secret123!", description = "Contraseña de acceso segura")
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @Schema(example = "juan@example.com", description = "Correo electronico de contacto")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @Schema(example = "ROLE_MEMBER", description = "Rol asignado al usuario (ADMIN, OWNER, etc.)")
        @NotNull(message = "El rol es obligatorio")
        Role role,

        @Schema(example = "Mi Hogar", description = "Datos del hogar que se asignara al usuario")
        @NotNull(message = "La información del hogar es obligatoria")
        String householdName,

        @Schema(example = "[\"uuid-alergeno-1\", \"uuid-alergeno-2\"]", description = "Lista de IDs de alérgenos del usuario")
        List<String> allergenIds,

        @Schema(example = "[\"HORNO\", \"AIR_FRYER\"]", description = "Lista de utensilios del hogar")
        List<Appliance> applianceTypes
) {}