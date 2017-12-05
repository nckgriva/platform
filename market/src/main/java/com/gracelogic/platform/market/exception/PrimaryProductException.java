package com.gracelogic.platform.market.exception;

public class PrimaryProductException extends Exception {

    private String message;

    public PrimaryProductException(String message) {
        super(message);
        this.message = message;
    }

    public PrimaryProductException() {
        super("");
    }

    public String getMessage() {
        return message;
    }
}
