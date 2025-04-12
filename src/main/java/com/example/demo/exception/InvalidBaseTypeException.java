package com.example.demo.exception;

public class InvalidBaseTypeException extends BaseException{
    public InvalidBaseTypeException() {
    }

    public InvalidBaseTypeException(String message) {
        super(message);
    }

    public InvalidBaseTypeException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public InvalidBaseTypeException(String message, Throwable cause) {
        super(message, cause);
    }


    public InvalidBaseTypeException(Throwable cause) {
        super(cause);
    }

    public InvalidBaseTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidBaseTypeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
