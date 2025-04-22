package com.userinfo.controller.dto;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    HttpStatus status;
    private String message;

    public CustomException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}