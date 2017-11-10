package com.gracelogic.platform.payment.exception;

public class PaymentExecutionException extends Exception {

    private String message;

    public PaymentExecutionException(String message) {
        super(message);
        this.message = message;
    }

    public PaymentExecutionException() {
        super("");
    }

    public String getMessage() {
        return message;
    }
}
