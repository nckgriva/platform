package com.gracelogic.platform.user.api;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class AbstractAuthorizedController {
    protected static final Logger logger = Logger.getLogger(AbstractAuthorizedController.class);

    protected AuthorizedUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getDetails() != null
                && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof AuthorizedUser) {
            return (AuthorizedUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        }
        return null;
    }
}