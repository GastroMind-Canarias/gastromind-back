package com.gastromind.api.domain.exceptions;

/**
 * Excepción de dominio por error al procesar imágenes.
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
