package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class TooFastOperationException extends AuthenticationException {
    public TooFastOperationException() {
        super("");
    }

    public TooFastOperationException(String msg) {
        super(msg);
    }

    public TooFastOperationException(String msg, Throwable t) {
        super(msg, t);
    }


}
