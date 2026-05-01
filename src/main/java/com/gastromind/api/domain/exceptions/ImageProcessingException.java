package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio por error al procesar imAgenes.
 */
public class ImageProcessingException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public ImageProcessingException(String message) {
        super(message);
    }
}
