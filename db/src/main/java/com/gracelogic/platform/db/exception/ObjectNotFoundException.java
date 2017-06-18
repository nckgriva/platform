package com.gracelogic.platform.db.exception;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
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
