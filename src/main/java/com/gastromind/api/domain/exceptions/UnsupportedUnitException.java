package com.gastromind.api.domain.exceptions;

/**
 * Excepción de dominio para unidades de medida no soportadas.
 */
public class UnsupportedUnitException extends RuntimeException {
    /**
     * Crea una nueva instancia.
     * @param message detalle del error
     */
    public UnsupportedUnitException(String message) {
        super(message);
    }
}
