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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CardNotFoundException.class)
    public RestExceptionResponse handleCardNotFoundException(CardNotFoundException e, HttpServletRequest req) {
        log.error("Card not found. Request: {} {}", req.getMethod(), req.getRequestURI());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.CARD_NOT_FOUND_BY_ID.name(),
                e.getMessage(),
                req
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public RestExceptionResponse handleBadRequestExceptions(BaseException e, HttpServletRequest req) {
        String errorCode = (e instanceof BaseValidationException) ?
                ErrorCode.VALIDATION_ERROR.name() :
                ErrorCode.INVALID_REQUEST_DATA.name();

        log.warn("Bad request. Error: {}, Path: {}", e.getMessage(), req.getRequestURI());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorCode,
                e.getMessage(),
                req
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<String> errors = getValidationErrors(e.getBindingResult());
        log.warn("Validation errors: {}, Path: {}", errors, req.getRequestURI());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.name(),
                formatValidationErrors(errors),
                req
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public RestExceptionResponse handleAllExceptions(Exception e, HttpServletRequest req) {
        log.error("Internal error. Path: {}, Error: ", req.getRequestURI(), e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                ErrorCode.INTERNAL_SERVER_ERROR.getFormattedMessage(),
                req
        );
    }

    private RestExceptionResponse buildErrorResponse(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
        return new RestExceptionResponse(
                status.value(),
                errorCode,
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
    }

    private List<String> getValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
    }

    private String formatFieldError(FieldError fieldError) {
        return String.format("%s: %s",
                fieldError.getField(),
                Optional.ofNullable(fieldError.getDefaultMessage())
                        .orElse(ErrorCode.INVALID_REQUEST_DATA.getFormattedMessage()));
    }

    private String formatValidationErrors(List<String> errors) {
        return errors.isEmpty() ?
                ErrorCode.VALIDATION_ERROR.getFormattedMessage() :
                String.join("; ", errors);
    }
}