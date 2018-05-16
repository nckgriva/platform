package com.gracelogic.platform.survey.exception;

public class HitRespondentsLimitException extends Exception {

    private String message;

    public HitRespondentsLimitException(String message) {
        super(message);
        this.message = message;
    }

    public HitRespondentsLimitException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
