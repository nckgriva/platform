package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class TooManyAttemptsException extends AuthenticationException {
    public TooManyAttemptsException() {
        super("");
    }

    public TooManyAttemptsException(String msg) {
        super(msg);
    }

    public TooManyAttemptsException(String msg, Throwable t) {
        super(msg, t);
    }
}
