package com.gracelogic.platform.notification.exception;

public class SendingException extends Exception {

    private String message;

    public SendingException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
