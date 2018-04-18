package com.gracelogic.platform.market.exception;

public class InvalidProductException extends Exception {

    private String message;

    public InvalidProductException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidProductException() {
    }

    public String getMessage() {
        return message;
    }
}
