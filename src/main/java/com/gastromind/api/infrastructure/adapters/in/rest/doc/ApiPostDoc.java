package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiSecurityAndGlobalErrors 
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Recurso creado con éxito"),
    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta - Datos de entrada inválidos")
})
public @interface ApiPostDoc {}
