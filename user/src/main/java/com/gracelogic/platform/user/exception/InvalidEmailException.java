package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
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
