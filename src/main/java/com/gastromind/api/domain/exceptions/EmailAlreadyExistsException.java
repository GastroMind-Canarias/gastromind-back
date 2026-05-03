package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio cuando el email ya estA registrado.
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
