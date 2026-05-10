package com.gastromind.api.domain.exceptions;

/**
 * El identificador solicitado no existe en persistencia; suele mapearse a 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
