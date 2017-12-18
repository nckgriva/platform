package com.gracelogic.platform.account.exception;

public class CurrencyMismatchException extends Exception {

    public CurrencyMismatchException(String message) {
        super(message);
    }

    public CurrencyMismatchException() {
        super();
    }
}
