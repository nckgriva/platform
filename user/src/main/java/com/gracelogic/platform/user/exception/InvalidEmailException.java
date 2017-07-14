package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidEmailException extends AuthenticationException {
    public InvalidEmailException() {
        super("");
    }

    public InvalidEmailException(String msg) {
        super(msg);
    }

    public InvalidEmailException(String msg, Throwable t) {
        super(msg, t);
    }


}
