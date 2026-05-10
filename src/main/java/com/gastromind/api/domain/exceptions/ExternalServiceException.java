package com.gastromind.api.domain.exceptions;

/**
 * Error al hablar con un proveedor externo (Gemini, almacenamiento, etc.); el mensaje debería ser operativo.
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}
