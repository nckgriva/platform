package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
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
