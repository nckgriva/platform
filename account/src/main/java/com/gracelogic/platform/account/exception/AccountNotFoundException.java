package com.gracelogic.platform.account.exception;

public class AccountNotFoundException extends Exception {

    private String message;

    public AccountNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public AccountNotFoundException() {
    }

    public String getMessage() {
        return message;
    }
}
