package com.gracelogic.platform.survey.exception;

public class LogicDependencyException extends Exception {

    private String message;

    public LogicDependencyException(String message) {
        super(message);
        this.message = message;
    }

    public LogicDependencyException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
