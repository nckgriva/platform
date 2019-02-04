package com.gracelogic.platform.user.filter;

import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LocaleFilter extends AbstractAuthorizedController implements Filter {
    public static final String SESSION_ATTRIBUTE_LOCALE = "session_locale";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String locale = null;

            AuthorizedUser user = getUser();
            if (user != null && !StringUtils.isEmpty(user.getLocale())) {
                locale = user.getLocale();
            }
            else if (httpRequest.getSession(false) != null) {
                try {
                    locale = (String) httpRequest.getSession(false).getAttribute(SESSION_ATTRIBUTE_LOCALE);
                }
                catch (Exception ignored) {}
            }

            if (!StringUtils.isEmpty(locale)) {
                LocaleHolder.setLocale(locale);
            }
            else {
                LocaleHolder.setLocale(LocaleHolder.defaultLocale);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
