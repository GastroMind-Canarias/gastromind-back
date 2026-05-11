package com.gastromind.api.domain.exceptions;

/**
 * Fallo al extraer líneas de ticket desde imagen con IA.
 */
public class AiTicketException extends RuntimeException {

    public AiTicketException(String message) {
        super(message);
    }

    public AiTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
