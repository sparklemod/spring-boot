package com.userinfo.controller.exception;

import com.userinfo.controller.dto.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        return new ResponseEntity<>(new ErrorResponse(new Date(), e.getStatus().value(), e.getMessage()),
                                    e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                         .getAllErrors()
                         .stream()
                         .map(DefaultMessageSourceResolvable::getDefaultMessage)
                         .collect(Collectors.joining(", "));

        return new ResponseEntity<>(new ErrorResponse(new Date(), e.getStatusCode().value(), errors),
                                    e.getStatusCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleForbiddenException(AccessDeniedException e) {
        return new ResponseEntity<>(new ErrorResponse(new Date(), HttpStatus.FORBIDDEN.value(), e.getMessage()),
                                    HttpStatus.FORBIDDEN);
    }
}
