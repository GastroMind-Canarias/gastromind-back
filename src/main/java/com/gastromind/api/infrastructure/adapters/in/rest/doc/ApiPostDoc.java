package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-anotación para POST exitoso (201) y validación (400), más seguridad y errores globales compuestos.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiSecurityAndGlobalErrors
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
    @ApiResponse(responseCode = "400", description = "Solicitud invAlida")
})
public @interface ApiPostDoc {}
