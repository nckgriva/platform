package com.gracelogic.platform.survey.exception;

public class BadDTOException extends Exception{
    private String message;

    public BadDTOException(String message) {
        super(message);
        this.message = message;
    }

    public BadDTOException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
