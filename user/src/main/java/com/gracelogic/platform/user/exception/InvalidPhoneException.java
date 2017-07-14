package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidPhoneException extends AuthenticationException {
    public InvalidPhoneException() {
        super("");
    }

    public InvalidPhoneException(String msg) {
        super(msg);
    }

    public InvalidPhoneException(String msg, Throwable t) {
        super(msg, t);
    }


}
