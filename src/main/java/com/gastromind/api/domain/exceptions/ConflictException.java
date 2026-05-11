package com.gastromind.api.domain.exceptions;

/**
 * Estado incompatible con la operación (409): duplicados, transiciones ilegales, etc.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
