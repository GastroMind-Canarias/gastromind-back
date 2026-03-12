package com.gastromind.api.infrastructure.security.auth.dtos;

import java.util.List;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

        @Schema(example = "[{ \"id\": 1, \"name\": \"Gluten\" }]", description = "Datos de los alergenos del usuario")
        @NotNull(message = "La información de los alergenos es obligatoria")
        List<Allergen> allergens

) {}