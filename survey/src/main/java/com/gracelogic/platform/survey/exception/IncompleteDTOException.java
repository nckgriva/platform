package com.gracelogic.platform.survey.exception;

public class IncompleteDTOException extends Exception{
    private String message;

    public IncompleteDTOException(String message) {
        super(message);
        this.message = message;
    }

    public IncompleteDTOException() {
        super("");
        message = null;
    }

    public String getMessage() {
        return message;
    }
}
