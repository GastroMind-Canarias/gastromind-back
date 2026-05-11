package com.gastromind.api.domain.exceptions;

/**
 * Registro rechazado porque el correo ya está asociado a otra cuenta.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
