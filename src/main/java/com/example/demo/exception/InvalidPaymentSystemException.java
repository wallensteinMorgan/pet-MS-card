package com.example.demo.exception;

public class InvalidPaymentSystemException extends BaseException {
    public InvalidPaymentSystemException() {
    }

    public InvalidPaymentSystemException(String message) {
        super(message);
    }

    public InvalidPaymentSystemException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public InvalidPaymentSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPaymentSystemException(Throwable cause) {
        super(cause);
    }

    public InvalidPaymentSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}