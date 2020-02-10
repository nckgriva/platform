package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class UserBlockedException extends AuthenticationException {
    public UserBlockedException() {
        super("");
    }

    public UserBlockedException(String msg) {
        super(msg);
    }

    public UserBlockedException(String msg, Throwable t) {
        super(msg, t);
    }
}
