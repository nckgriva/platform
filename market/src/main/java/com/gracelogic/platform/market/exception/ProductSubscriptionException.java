package com.gracelogic.platform.market.exception;

public class ProductSubscriptionException extends Exception {

    private String message;

    public ProductSubscriptionException(String message) {
        super(message);
        this.message = message;
    }

    public ProductSubscriptionException() {
        super("");
    }

    public String getMessage() {
        return message;
    }
}
