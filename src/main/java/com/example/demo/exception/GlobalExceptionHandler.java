package com.example.demo.exception;

import com.example.demo.dto.RestExceptionResponse;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CardNotFoundException.class)
    public RestExceptionResponse handleCardNotFoundException(CardNotFoundException e, HttpServletRequest req) {
        log.error(e.getMessage());
        return new RestExceptionResponse(HttpStatus.NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public RestExceptionResponse handleBadRequestExceptions(BaseException e, HttpServletRequest req) {
        log.error(e.getMessage());
        return new RestExceptionResponse(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<String> defaultMessage = getDefaultMessagesFromMethodArgumentException(e.getBindingResult());
        log.error(defaultMessage.toString());
        return new RestExceptionResponse(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), defaultMessage.toString(), LocalDateTime.now(), req.getRequestURI());
    }

    private List<String> getDefaultMessagesFromMethodArgumentException(BindingResult bindingResult) {
        List<String> fieldErrorDefaultMessages = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrorDefaultMessages.add(fieldError.getDefaultMessage());
        }
        return fieldErrorDefaultMessages;
    }
}
