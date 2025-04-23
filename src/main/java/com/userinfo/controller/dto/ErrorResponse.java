package com.userinfo.controller.dto;

import java.util.Date;

public record ErrorResponse(Date timestamp, int status, String message) {
}