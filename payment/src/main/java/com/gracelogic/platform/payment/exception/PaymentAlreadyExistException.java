package com.gracelogic.platform.payment.exception;

public class PaymentAlreadyExistException extends Exception {

    private String message;

    public PaymentAlreadyExistException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
