package com.gracelogic.platform.survey.exception;

public class InternalErrorException extends Exception{
    private String message;

    public InternalErrorException(String message) {
        super(message);
        this.message = message;
    }

    public InternalErrorException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
