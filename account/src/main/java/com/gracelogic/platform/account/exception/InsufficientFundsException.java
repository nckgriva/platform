package com.gracelogic.platform.account.exception;

public class InsufficientFundsException extends Exception {

    private String message;

    public InsufficientFundsException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
