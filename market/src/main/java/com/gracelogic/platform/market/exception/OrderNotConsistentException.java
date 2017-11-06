package com.gracelogic.platform.market.exception;

public class OrderNotConsistentException extends Exception {

    private String message;

    public OrderNotConsistentException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
