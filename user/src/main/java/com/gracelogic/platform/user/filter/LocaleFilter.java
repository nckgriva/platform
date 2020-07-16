package com.gracelogic.platform.user.filter;

import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LocaleFilter extends AbstractAuthorizedController implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String acceptLanguage = httpRequest.getHeader("Accept-Language");
            String locale = null;

            AuthorizedUser user = getUser();
            if (user != null && !StringUtils.isEmpty(user.getLocale())) {
                locale = user.getLocale();
            } else if (!StringUtils.isEmpty(acceptLanguage)) {
                try {
                    locale = LocaleUtils.toLocale(acceptLanguage).getLanguage();
                } catch (Exception ignored) {
                }
            }

            if (!StringUtils.isEmpty(locale)) {
                LocaleHolder.setLocale(locale);
            } else {
                LocaleHolder.setLocale(LocaleHolder.defaultLocale);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
