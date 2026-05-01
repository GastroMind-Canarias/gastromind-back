package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio cuando un recurso no existe.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public NotFoundException(String message) {
        super(message);
    }
}
