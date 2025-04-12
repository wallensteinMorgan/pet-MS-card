package com.example.demo.exception;

public class BaseValidationException  extends BaseException{
    public BaseValidationException() {
    }

    public BaseValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public BaseValidationException(String message) {
        super(message);
    }

    public BaseValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseValidationException(Throwable cause) {
        super(cause);
    }

    public BaseValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
