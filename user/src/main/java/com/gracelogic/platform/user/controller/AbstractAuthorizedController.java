package com.gracelogic.platform.user.controller;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Author: Igor Parkhomenko
 * Date: 18.12.14
 * Time: 16:50
 */
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
