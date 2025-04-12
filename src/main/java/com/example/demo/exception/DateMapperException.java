package com.example.demo.exception;

public class DateMapperException extends BaseException{
    public DateMapperException() {
    }

    public DateMapperException(String message) {
        super(message);
    }

    public DateMapperException(ErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
    }

    public DateMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateMapperException(Throwable cause) {
        super(cause);
    }

    public DateMapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
