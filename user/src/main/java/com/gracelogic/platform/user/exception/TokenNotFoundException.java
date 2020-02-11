package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenNotFoundException extends AuthenticationException {

    public TokenNotFoundException() {
        super("");
    }

    public TokenNotFoundException(String msg) {
        super(msg);
    }

    public TokenNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
