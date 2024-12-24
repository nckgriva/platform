package com.gracelogic.platform.user.filter;

import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.service.LastSessionHolder;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LastSessionFilter extends AbstractAuthorizedController implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            AuthorizedUser user = getUser();
            if (user != null) {
                HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
                if (session != null && !LastSessionHolder.isLastSession(user.getId(), session.getId())) {
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                }
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
