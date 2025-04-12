package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record  RestExceptionResponse(Integer httpStatusCode, String exception, String message, LocalDateTime timestamp, String path) {
}

