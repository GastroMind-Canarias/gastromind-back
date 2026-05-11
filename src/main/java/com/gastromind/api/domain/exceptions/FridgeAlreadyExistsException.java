package com.gastromind.api.domain.exceptions;

/**
 * El hogar ya tiene una nevera; no se permite crear otra sin cerrar la existente.
 */
public class FridgeAlreadyExistsException extends RuntimeException {
    public FridgeAlreadyExistsException(String message) {
        super(message);
    }
}
