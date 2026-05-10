package com.gastromind.api.domain.exceptions;

/**
 * La imagen del ticket no se pudo leer o convertir antes de enviarla al modelo.
 */
public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String message) {
        super(message);
    }
}
