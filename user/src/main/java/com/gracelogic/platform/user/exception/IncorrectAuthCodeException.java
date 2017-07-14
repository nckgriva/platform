package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class IncorrectAuthCodeException extends AuthenticationException {
    public IncorrectAuthCodeException() {
        super("");
    }

    public IncorrectAuthCodeException(String msg) {
        super(msg);
    }

    public IncorrectAuthCodeException(String msg, Throwable t) {
        super(msg, t);
    }


}
