package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotApprovedException extends AuthenticationException {
    public UserNotApprovedException() {
        super("");
    }

    public UserNotApprovedException(String msg) {
        super(msg);
    }

    public UserNotApprovedException(String msg, Throwable t) {
        super(msg, t);
    }
}
