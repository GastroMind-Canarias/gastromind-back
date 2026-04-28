package com.gastromind.api.domain.exceptions;

/**
 * Excepción de dominio cuando el email ya está registrado.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
