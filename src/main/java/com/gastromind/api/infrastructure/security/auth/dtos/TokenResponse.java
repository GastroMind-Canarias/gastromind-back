package com.gastromind.api.infrastructure.security.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta que contiene el token de acceso generado tras una autenticacion exitosa")
public record TokenResponse(

        @Schema(
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                description = "Token JWT (JSON Web Token) que debe incluirse en la cabecera Authorization de las peticiones protegidas"
        )
        String token
) {}