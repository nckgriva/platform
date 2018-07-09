package com.gracelogic.platform.survey.exception;

public class MaxAttemptsHitException extends Exception {

    private String message;

    public MaxAttemptsHitException(String message) {
        super(message);
        this.message = message;
    }

    public MaxAttemptsHitException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
