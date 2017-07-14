package com.gracelogic.platform.db.exception;

public class ObjectNotFoundException extends Exception {
    public ObjectNotFoundException() {
        super("");
    }

    public ObjectNotFoundException(String msg) {
        super(msg);
    }

    public ObjectNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
