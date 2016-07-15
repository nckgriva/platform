package com.gracelogic.platform.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
public class NotAllowedIPException extends AuthenticationException {
    public NotAllowedIPException(String msg) {
        super(msg);
    }

    public NotAllowedIPException(String msg, Throwable t) {
        super(msg, t);
    }
}
