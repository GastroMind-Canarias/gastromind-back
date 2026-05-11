package com.gastromind.api.infrastructure.adapters.in.rest.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documentación OpenAPI para POST que invocan proveedor de IA (p. ej. Gemini).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiSecurityAndGlobalErrors
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida (cuerpo, multipart o validacion)"),
        @ApiResponse(responseCode = "429", description = "Limite de cuota o peticiones del proveedor de IA"),
        @ApiResponse(responseCode = "503", description = "Proveedor de IA no disponible, error de red o respuesta no interpretable")
})
public @interface ApiPostExternalAiDoc {}
