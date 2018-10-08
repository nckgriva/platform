package com.gracelogic.platform.survey.exception;

public class MaxAttemptsException extends Exception {

    private String message;

    public MaxAttemptsException(String message) {
        super(message);
        this.message = message;
    }

    public MaxAttemptsException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
