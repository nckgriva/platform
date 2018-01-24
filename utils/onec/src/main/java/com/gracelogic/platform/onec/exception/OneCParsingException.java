package com.gracelogic.platform.onec.exception;

public class OneCParsingException extends RuntimeException {
    private Integer lineNumber = 0;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
}
