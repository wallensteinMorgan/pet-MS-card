package com.example.demo.exception;

public class BaseTransactionException extends BaseException{
    public BaseTransactionException() {
    }

    public BaseTransactionException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public BaseTransactionException(String message) {
        super(message);
    }

    public BaseTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseTransactionException(Throwable cause) {
        super(cause);
    }

    public BaseTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
