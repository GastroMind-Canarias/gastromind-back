package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio cuando el hogar ya tiene nevera.
 */
public class FridgeAlreadyExistsException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public FridgeAlreadyExistsException(String message) {
        super(message);
    }
}
