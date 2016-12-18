package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
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
