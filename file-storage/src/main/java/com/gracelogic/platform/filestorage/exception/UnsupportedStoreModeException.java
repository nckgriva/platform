package com.gracelogic.platform.filestorage.exception;

public class UnsupportedStoreModeException extends Exception {

    private String message;

    public UnsupportedStoreModeException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
