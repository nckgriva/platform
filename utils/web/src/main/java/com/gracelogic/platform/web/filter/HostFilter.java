package com.gracelogic.platform.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HostFilter implements Filter {
    private static final String LOCAL_HOST = "localhost";
    private static final String GENERAL_HOST = "sp.join-and-buy.ru";
    private static final String GENERAL_HOST_WITH_HTTP = "http://" + GENERAL_HOST;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

            if (!httpRequest.getServerName().toLowerCase().contains(GENERAL_HOST) && !httpRequest.getServerName().toLowerCase().contains(LOCAL_HOST)) {
                httpResponse.sendRedirect(GENERAL_HOST_WITH_HTTP);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
