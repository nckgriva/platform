package com.gracelogic.platform.payment.exception;

public class InvalidPaymentSystemException extends Exception {

    private String message;

    public InvalidPaymentSystemException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
