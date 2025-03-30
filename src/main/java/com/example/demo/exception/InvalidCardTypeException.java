package com.example.demo.exception;

public class InvalidCardTypeException extends RuntimeException {
    public InvalidCardTypeException(String message) {
        super(message);
    }
}