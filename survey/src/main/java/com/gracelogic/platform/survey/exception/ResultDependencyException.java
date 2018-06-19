package com.gracelogic.platform.survey.exception;

public class ResultDependencyException extends Exception {
    private String message;

    public ResultDependencyException(String message) {
        super(message);
        this.message = message;
    }

    public ResultDependencyException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
