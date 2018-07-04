package com.gracelogic.platform.survey.exception;

public class UnansweredException extends Exception {
    private String message;

    public UnansweredException(String message) {
        super(message);
        this.message = message;
    }

    public UnansweredException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
