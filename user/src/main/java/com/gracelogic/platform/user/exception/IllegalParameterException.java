package com.gracelogic.platform.user.exception;

public class IllegalParameterException extends Exception {

    private String message;

    public IllegalParameterException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
