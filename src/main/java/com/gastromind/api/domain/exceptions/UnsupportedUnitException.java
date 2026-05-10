package com.gastromind.api.domain.exceptions;

/**
 * La unidad del ticket o del catálogo no se puede convertir al sistema interno.
 */
public class UnsupportedUnitException extends RuntimeException {
    public UnsupportedUnitException(String message) {
        super(message);
    }
}
