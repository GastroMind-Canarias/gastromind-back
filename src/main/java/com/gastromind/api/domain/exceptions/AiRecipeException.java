package com.gastromind.api.domain.exceptions;

/**
 * Fallo al generar o ajustar recetas vía IA (clave, cuota, formato de respuesta).
 */
public class AiRecipeException extends RuntimeException {

    public AiRecipeException(String message) {
        super(message);
    }

    public AiRecipeException(String message, Throwable cause) {
        super(message, cause);
    }
}
