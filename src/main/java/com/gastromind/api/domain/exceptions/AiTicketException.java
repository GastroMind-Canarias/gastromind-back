package com.gastromind.api.domain.exceptions;

public class AiTicketException extends RuntimeException {

    public AiTicketException(String message) {
        super(message);
    }

    public AiTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
