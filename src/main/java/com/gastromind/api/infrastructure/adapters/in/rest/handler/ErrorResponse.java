package com.gastromind.api.infrastructure.adapters.in.rest.handler;

import java.time.LocalDateTime;

/**
 * Representa error response dentro del dominio de la aplicacion.
 */
public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}






