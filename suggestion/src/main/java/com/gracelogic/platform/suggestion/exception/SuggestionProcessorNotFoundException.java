package com.gracelogic.platform.suggestion.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Author: Igor Parkhomenko
 * Date: 28.08.2015
 * Time: 17:06
 */
public class SuggestionProcessorNotFoundException extends AuthenticationException {
    public SuggestionProcessorNotFoundException() {
        super("");
    }

    public SuggestionProcessorNotFoundException(String msg) {
        super(msg);
    }

    public SuggestionProcessorNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
