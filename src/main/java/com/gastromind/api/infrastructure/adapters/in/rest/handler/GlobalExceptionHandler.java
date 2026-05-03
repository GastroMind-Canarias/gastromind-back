package com.gastromind.api.infrastructure.adapters.in.rest.handler;

import com.gastromind.api.domain.exceptions.AiRecipeException;
import com.gastromind.api.domain.exceptions.AiTicketException;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.FridgeAlreadyExistsException;
import com.gastromind.api.domain.exceptions.ImageProcessingException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.exceptions.UnsupportedUnitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;

@RestControllerAdvice
/**
 * Representa global exception handler dentro del dominio de la aplicacion.
 */
public class GlobalExceptionHandler {
    /**
     * Realiza handle forbidden exception.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    /**
     * Realiza handle unauthorized.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class, AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    /**
     * Realiza handle access denied.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    /**
     * Realiza handle not found exception.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    /**
     * Realiza handle fridge already exists exception.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(FridgeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFridgeAlreadyExistsException(FridgeAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    /**
     * Realiza handle ai recipe exception.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(AiRecipeException.class)
    public ResponseEntity<ErrorResponse> handleAiRecipeException(AiRecipeException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }
    /**
     * Realiza handle ai ticket exception.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(AiTicketException.class)
    public ResponseEntity<ErrorResponse> handleAiTicketException(AiTicketException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    /**
     * Realiza handle bad request.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException validationException
                && validationException.getBindingResult().getFieldError() != null) {
            message = validationException.getBindingResult().getFieldError().getDefaultMessage();
        }
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }
    /**
     * Realiza handle response status.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return buildResponse(HttpStatus.valueOf(ex.getStatusCode().value()), message);
    }
    /**
     * Realiza handle generic error.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenericError(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    /**
     * Realiza handle domain bad request.
     * @param ex valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @ExceptionHandler({ ImageProcessingException.class, UnsupportedUnitException.class })
    public ResponseEntity<ErrorResponse> handleDomainBadRequest(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

}




