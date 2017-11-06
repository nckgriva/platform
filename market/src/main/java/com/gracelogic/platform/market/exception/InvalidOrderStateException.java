package com.gracelogic.platform.market.exception;

public class InvalidOrderStateException extends Exception {

    private String message;

    public InvalidOrderStateException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidOrderStateException() {
        super("");
    }

    public String getMessage() {
        return message;
    }
}
