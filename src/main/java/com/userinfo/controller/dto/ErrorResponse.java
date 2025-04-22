package com.userinfo.controller.dto;

import java.util.Date;

public class ErrorResponse {
    private Date timestamp;
    private String message;
    private int status;

    public ErrorResponse(Date timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return this.message;
    }
}