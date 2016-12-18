package com.gracelogic.platform.user.exception;

public class CustomLocalizedException extends Exception {

    private String message;

    public CustomLocalizedException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
