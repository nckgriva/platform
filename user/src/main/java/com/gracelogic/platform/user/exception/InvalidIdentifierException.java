package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidIdentifierException extends AuthenticationException {
    public InvalidIdentifierException() {
        super("");
    }

    public InvalidIdentifierException(String msg) {
        super(msg);
    }

    public InvalidIdentifierException(String msg, Throwable t) {
        super(msg, t);
    }


}
