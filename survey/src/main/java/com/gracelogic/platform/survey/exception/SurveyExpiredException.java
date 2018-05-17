package com.gracelogic.platform.survey.exception;

public class SurveyExpiredException extends Exception {

    private String message;

    public SurveyExpiredException(String message) {
        super(message);
        this.message = message;
    }

    public SurveyExpiredException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
