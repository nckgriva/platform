package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {
    public InvalidPasswordException() {
        super("");
    }

    public InvalidPasswordException(String msg) {
        super(msg);
    }

    public InvalidPasswordException(String msg, Throwable t) {
        super(msg, t);
    }


}
