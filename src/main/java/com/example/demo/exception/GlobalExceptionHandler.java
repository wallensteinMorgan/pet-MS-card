package com.example.demo.exception;

import com.example.demo.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCardTypeException.class)
    public ResponseEntity<ErrorDetails> handleInvalidCardTypeException(InvalidCardTypeException ex) {
        log.error("Card type error: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPaymentSystemException.class)
    public ResponseEntity<ErrorDetails> handleInvalidPaymentSystemException(InvalidPaymentSystemException ex) {
        log.error("Payment system error: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(400, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Error reading the request body: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(400, "Incorrect data format in the request");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Data validation error: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(400, "Data validation error");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDetails> handleIOException(IOException ex) {
        log.error("Error during data processing: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(400, "Incorrect date or data format in the request");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception ex) {
        log.error("An error has occurred: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(500, "Server error: " + ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
