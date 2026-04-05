package com.gastromind.api.domain.exceptions;

public class AiRecipeException extends RuntimeException {

    public AiRecipeException(String message) {
        super(message);
    }

    public AiRecipeException(String message, Throwable cause) {
        super(message, cause);
    }
}
