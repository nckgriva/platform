package com.gracelogic.platform.user.api;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class AbstractAuthorizedController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected AuthorizedUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getDetails() != null
                && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof AuthorizedUser) {
            return (AuthorizedUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        }
        return null;
    }
}