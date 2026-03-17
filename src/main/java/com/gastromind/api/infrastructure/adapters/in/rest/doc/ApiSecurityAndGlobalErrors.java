package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inexistente o inválido"),
    @ApiResponse(responseCode = "403", description = "Prohibido - No tienes los permisos necesarios"),
    @ApiResponse(responseCode = "500", description = "Error interno - Fallo no controlado en el servidor")
})
public @interface ApiSecurityAndGlobalErrors {}
