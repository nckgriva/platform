package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidNickException extends AuthenticationException {
    public InvalidNickException() {
        super("");
    }

    public InvalidNickException(String msg) {
        super(msg);
    }

    public InvalidNickException(String msg, Throwable t) {
        super(msg, t);
    }


}
