package com.gracelogic.platform.suggestion.exception;

import org.springframework.security.core.AuthenticationException;

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
