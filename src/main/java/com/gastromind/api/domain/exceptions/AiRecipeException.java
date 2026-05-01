package com.gastromind.api.domain.exceptions;

/**
 * ExcepciAn de dominio en generaciAn de recetas con IA.
 */
public class AiRecipeException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */

    public AiRecipeException(String message) {
        super(message);
    }
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     * @param cause valor a utilizar.
     */

    public AiRecipeException(String message, Throwable cause) {
        super(message, cause);
    }
}
