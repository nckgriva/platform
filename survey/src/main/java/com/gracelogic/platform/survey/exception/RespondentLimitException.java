package com.gracelogic.platform.survey.exception;

public class RespondentLimitException extends Exception {

    private String message;

    public RespondentLimitException(String message) {
        super(message);
        this.message = message;
    }

    public RespondentLimitException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}