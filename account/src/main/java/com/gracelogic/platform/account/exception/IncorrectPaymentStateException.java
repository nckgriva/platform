package com.gracelogic.platform.account.exception;

public class IncorrectPaymentStateException extends Exception {

    private String message;

    public IncorrectPaymentStateException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
