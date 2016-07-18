package com.gracelogic.platform.web.filter;

import com.gracelogic.platform.web.service.LocaleHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

public class LocaleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            final String header = httpRequest.getHeader("Accept-Language");

            if (header != null) {
                LocaleHolder.setLocale(header);
            } else {
                LocaleHolder.setLocale(Locale.ENGLISH);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }


}
