package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class NotAllowedIPException extends AuthenticationException {
    public NotAllowedIPException() {
        super("");
    }

    public NotAllowedIPException(String msg) {
        super(msg);
    }

    public NotAllowedIPException(String msg, Throwable t) {
        super(msg, t);
    }
}
