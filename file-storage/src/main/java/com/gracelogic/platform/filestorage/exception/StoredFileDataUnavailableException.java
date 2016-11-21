package com.gracelogic.platform.filestorage.exception;

public class StoredFileDataUnavailableException extends Exception {

    private String message;

    public StoredFileDataUnavailableException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
