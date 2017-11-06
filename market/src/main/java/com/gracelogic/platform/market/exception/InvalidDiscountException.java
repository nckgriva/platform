package com.gracelogic.platform.market.exception;

public class InvalidDiscountException extends Exception {

    private String message;

    public InvalidDiscountException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidDiscountException() {
        super("");
    }

    public String getMessage() {
        return message;
    }
}
