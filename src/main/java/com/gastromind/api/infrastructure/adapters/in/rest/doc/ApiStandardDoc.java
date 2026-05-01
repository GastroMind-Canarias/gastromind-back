package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiSecurityAndGlobalErrors 
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
})
public @interface ApiStandardDoc {}

