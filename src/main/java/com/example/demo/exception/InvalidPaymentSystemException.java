package com.example.demo.exception;

public class InvalidPaymentSystemException extends RuntimeException {
    public InvalidPaymentSystemException(String message) {
        super(message);
    }
}