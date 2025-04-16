package com.example.demo.exception;

public class RedisOperationException extends BaseException{
    public RedisOperationException(String message) {
        super(message);
    }
    public RedisOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

