package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
public class TooManyAttemptsException extends AuthenticationException {
    public TooManyAttemptsException(String msg) {
        super(msg);
    }

    public TooManyAttemptsException(String msg, Throwable t) {
        super(msg, t);
    }
}
