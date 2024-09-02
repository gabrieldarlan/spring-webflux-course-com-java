package com.gdarlan.webfluxcourse.controller.exceptions;

import com.gdarlan.webfluxcourse.service.exception.ObjectNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Mono<StandardError>> duplicateKeyException(
            DuplicateKeyException ex, ServerHttpRequest request
    ) {
        return ResponseEntity
                .badRequest()
                .body(Mono.just(
                        StandardError
                                .builder()
                                .timestamp(now())
                                .status(BAD_REQUEST.value())
                                .error(BAD_GATEWAY.getReasonPhrase())
                                .message(verifyDupKey(ex.getMessage()))
                                .path(request.getPath().toString())
                                .build()
                ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Mono<ValidationError>> validationError(
            WebExchangeBindException ex, ServerHttpRequest request
    ) {
        ValidationError error = new ValidationError(
                now(),
                request.getPath().toString(),
                BAD_REQUEST.value(),
                "Validation error",
                "Error on validation attributes"
        );

        for (FieldError x :
                ex.getBindingResult().getFieldErrors()) {
            error.addErrors(x.getField(), ex.getFieldError().getDefaultMessage());
        }


        return ResponseEntity.status(BAD_REQUEST)
                .body(Mono.just(error));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<Mono<StandardError>> objectNotFoundException(
            ObjectNotFoundException ex, ServerHttpRequest request
    ) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(Mono.just(
                        StandardError
                                .builder()
                                .timestamp(now())
                                .status(NOT_FOUND.value())
                                .error(NOT_FOUND.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getPath().toString())
                                .build()
                ));
    }

    private String verifyDupKey(String message) {
        return message.contains("email dup key") ? "E-mail already registered" : "Dup key exception";
    }

}
