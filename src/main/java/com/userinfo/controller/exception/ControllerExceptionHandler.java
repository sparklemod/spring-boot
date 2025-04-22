package com.userinfo.controller.exception;

import com.userinfo.controller.dto.CustomException;
import com.userinfo.controller.dto.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> handleRuntimeException2(CustomException e) {
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

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(401).body(new ErrorResponse(new Date(), 401, e.getMessage()));
    }
}
