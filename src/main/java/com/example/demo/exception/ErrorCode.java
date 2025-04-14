package com.example.demo.exception;


import lombok.Getter;

@Getter
public enum  ErrorCode {
    INSUFFICIENT_BALANCE("Insufficient funds on card %s for payment. Available: %s, required: %s"),

    INVALID_CARD_ID("Card ID cannot be null or less than 0"),

    INVALID_USER_ID("Card user ID cannot be null or less than 0"),

    INVALID_ACCOUNT_ID("Card account ID cannot be null or less than 0"),

    CARD_NOT_FOUND_BY_ID("Card with ID %s not found"),

    CARD_NOT_FOUND_BY_USER_ID("Card with user ID %s not found"),

    CARD_NOT_FOUND_BY_ACCOUNT_ID("Card with account ID %s not found"),

    INVALID_DATE_STRING("Invalid expiry date length. Expected 10 characters, but got: %s. Format: dd-MM-yyyy"),

    UNSUPPORTED_DATE_FORMAT("Unsupported date format. Expected format: dd-MM-yyyy"),

    VALIDATION_ERROR("Validation failed"),

    INVALID_REQUEST_DATA("Invalid request data"),

    INTERNAL_SERVER_ERROR("Internal server error");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
}
