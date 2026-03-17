package com.gastromind.api.infrastructure.security.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credenciales para el inicio de sesion")
public record LoginRequest(

        @Schema(example = "juan_gastro", description = "Nombre de usuario")
        @NotBlank(message = "El nombre de usuario no puede estar vacio")
        String username,

        @Schema(example = "Secret123!", description = "Contraseña de acceso")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password
) {}