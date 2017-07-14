package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

public class PhoneOrEmailIsNecessaryException extends AuthenticationException {
    public PhoneOrEmailIsNecessaryException() {
        super("");
    }

    public PhoneOrEmailIsNecessaryException(String msg) {
        super(msg);
    }

    public PhoneOrEmailIsNecessaryException(String msg, Throwable t) {
        super(msg, t);
    }


}
