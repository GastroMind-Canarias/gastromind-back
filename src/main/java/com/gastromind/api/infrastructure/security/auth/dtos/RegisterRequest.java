package com.gastromind.api.infrastructure.security.auth.dtos;

import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Registro: credenciales, hogar (crear o unirse) y preferencias. El rol efectivo lo asigna el servidor (OWNER al crear, MEMBER al unirse).")
public record RegisterRequest(

        @Schema(example = "juan_gastro", description = "Nombre de usuario único")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres")
        String username,

        @Schema(example = "Secret123!", description = "Contraseña de acceso segura")
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @Schema(example = "juan@example.com", description = "Correo electrónico")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @Schema(
                description = "Opcional; ignorado en el registro (el servidor asigna OWNER o MEMBER según el modo de hogar).",
                example = "ROLE_MEMBER",
                deprecated = true
        )
        Role role,

        @Schema(
                description = "Modo de hogar. Si no se envía, se infiere: JOIN_EXISTING si inviteToken tiene valor; si no, CREATE_NEW (requiere householdName).",
                example = "CREATE_NEW"
        )
        HouseholdRegistrationMode householdMode,

        @Schema(example = "Mi Hogar", description = "Nombre del hogar nuevo. Obligatorio si householdMode es CREATE_NEW (o sin token de invitación).")
        String householdName,

        @Schema(
                example = "invite_550e8400-e29b-41d4-a716-446655440000_21c8ec97-1cf9-46c7-a81c-3c24df66c20c",
                description = "Código de invitación del hogar existente. Si tiene valor, se une como MEMBER (se ignoran householdName y electrodomésticos del hogar)."
        )
        String inviteToken,

        @Schema(example = "[\"uuid-alergeno-1\"]", description = "IDs de alérgenos del usuario")
        List<String> allergenIds,

        @Schema(example = "[\"HORNO\", \"AIR_FRYER\"]", description = "Electrodomésticos del hogar; solo aplica al crear hogar nuevo")
        List<Appliance> applianceTypes
) {}
