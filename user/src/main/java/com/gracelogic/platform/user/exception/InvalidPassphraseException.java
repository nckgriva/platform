package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidPassphraseException extends AuthenticationException {
    public InvalidPassphraseException() {
        super("");
    }

    public InvalidPassphraseException(String msg) {
        super(msg);
    }

    public InvalidPassphraseException(String msg, Throwable t) {
        super(msg, t);
    }


}
