package com.gracelogic.platform.survey.exception;

public class HitTimeLimitException extends Exception {

    private String message;

    public HitTimeLimitException(String message) {
        super(message);
        this.message = message;
    }

    public HitTimeLimitException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
