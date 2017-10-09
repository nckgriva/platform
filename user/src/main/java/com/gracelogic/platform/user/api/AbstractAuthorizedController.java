package com.gracelogic.platform.user.api;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.model.UserSetting;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.localization.service.LocaleHolder;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

public abstract class AbstractAuthorizedController {
    protected static final Logger logger = Logger.getLogger(AbstractAuthorizedController.class);
    public static final String CURRENT_LOCALE = "CURRENT_LOCALE";

    @Autowired
    private UserService userService;

    protected AuthorizedUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getDetails() != null
                && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof AuthorizedUser) {
            return (AuthorizedUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        }
        return null;
    }

    protected Locale getUserLocale() {
       Locale locale = null;
        if (getUser() != null) {
            UserSetting storedLocale = userService.getUserSetting(getUser().getId(), CURRENT_LOCALE); //Todo: возможно стоит локаль кэшировать а не запрашивать её постоянно
            if (storedLocale != null) {
                try {
                    locale = LocaleUtils.toLocale(storedLocale.getValue());
                }
                catch (Exception e) {
                    logger.debug(String.format("Failed to resolve locale '%s'", storedLocale.getValue()), e);
                }
            }
        }

        if (locale == null) {
            locale = LocaleHolder.getLocale();
        }

        return locale;
    }
}
