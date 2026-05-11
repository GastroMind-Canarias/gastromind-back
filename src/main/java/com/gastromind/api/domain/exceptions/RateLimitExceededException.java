package com.gastromind.api.domain.exceptions;

/**
 * Demasiadas peticiones en ventana corta (p. ej. alias de tienda); el cliente debe esperar o reintentar.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
