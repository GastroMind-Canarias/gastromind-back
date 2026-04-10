package com.gastromind.api.domain.exceptions;

public class FridgeAlreadyExistsException extends RuntimeException {
    public FridgeAlreadyExistsException(String message) {
        super(message);
    }
}
