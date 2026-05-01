package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
    @ApiResponse(responseCode = "403", description = "No tienes permisos para esta operación"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
public @interface ApiSecurityAndGlobalErrors {}

