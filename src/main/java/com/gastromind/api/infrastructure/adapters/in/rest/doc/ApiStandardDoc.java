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
    @ApiResponse(responseCode = "200", description = "Operación realizada con éxito"),
    @ApiResponse(responseCode = "404", description = "No encontrado - El recurso solicitado no existe")
})
public @interface ApiStandardDoc {}
