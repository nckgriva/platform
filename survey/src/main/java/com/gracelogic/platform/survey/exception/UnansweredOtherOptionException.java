package com.gracelogic.platform.survey.exception;

public class UnansweredOtherOptionException extends Exception {
    private String message;
    public String questionText;

    public UnansweredOtherOptionException(String message, String questionText) {
        super(message);
        this.message = message;
        this.questionText = questionText;
    }

    protected UnansweredOtherOptionException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
