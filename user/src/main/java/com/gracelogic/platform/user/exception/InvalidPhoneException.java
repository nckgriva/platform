package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
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
