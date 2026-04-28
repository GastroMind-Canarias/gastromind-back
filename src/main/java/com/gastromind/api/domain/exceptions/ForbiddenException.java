package com.gastromind.api.domain.exceptions;

/**
 * Excepción de dominio cuando no hay permisos suficientes.
 */
public class ForbiddenException extends RuntimeException {
     /**
      * Crea una nueva instancia.
      * @param message detalle del error
      */
     public ForbiddenException(String message) {
        super(message);
    }
}
