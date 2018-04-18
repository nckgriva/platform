package com.gracelogic.platform.market.exception;

public class EmptyOrderException extends Exception {

    private String message;

    public EmptyOrderException(String message) {
        super(message);
        this.message = message;
    }

    public EmptyOrderException() {
    }

    public String getMessage() {
        return message;
    }
}
