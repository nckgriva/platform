package com.gracelogic.platform.market.exception;

public class InvalidCurrencyException extends Exception {

    private String message;

    public InvalidCurrencyException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidCurrencyException() {
    }

    public String getMessage() {
        return message;
    }
}
