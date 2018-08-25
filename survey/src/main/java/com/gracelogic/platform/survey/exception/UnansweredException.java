package com.gracelogic.platform.survey.exception;

public class UnansweredException extends Exception {
    private String message;
    public String questionName;

    public UnansweredException(String message, String questionName) {
        super(message);
        this.message = message;
        this.questionName = questionName;
    }

    protected UnansweredException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
