package com.gracelogic.platform.notification.exception;

public class TransportException extends Exception {

    public TransportException(String message) {
        super(message);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
