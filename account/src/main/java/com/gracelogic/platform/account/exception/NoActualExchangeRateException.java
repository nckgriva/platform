package com.gracelogic.platform.account.exception;

public class NoActualExchangeRateException extends Exception {

    public NoActualExchangeRateException(String message) {
        super(message);
    }

    public NoActualExchangeRateException() {
        super();
    }
}
