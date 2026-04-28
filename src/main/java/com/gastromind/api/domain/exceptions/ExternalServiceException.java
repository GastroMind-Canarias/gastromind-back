package com.gastromind.api.domain.exceptions;

/**
 * Excepción de dominio por fallo en servicios externos.
 */
public class ExternalServiceException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public ExternalServiceException(String message) {
        super(message);
    }
}
