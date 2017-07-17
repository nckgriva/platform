package com.gracelogic.platform.user.exception;

public class ForbiddenException extends Exception {

    private String message;

    public ForbiddenException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ForbiddenException() {
        super("");
        message = null;
    }
}
