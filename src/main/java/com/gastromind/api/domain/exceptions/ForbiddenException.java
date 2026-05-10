package com.gastromind.api.domain.exceptions;

/**
 * El usuario autenticado no puede realizar la operación sobre ese recurso (403).
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
