package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio en extracciAn de tickets con IA.
 */
public class AiTicketException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */

    public AiTicketException(String message) {
        super(message);
    }
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     * @param cause valor a utilizar.
     */

    public AiTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
