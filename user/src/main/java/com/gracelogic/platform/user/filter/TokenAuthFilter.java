package com.gracelogic.platform.user.filter;

import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.security.tokenAuth.TokenAuthentication;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

public class TokenAuthFilter implements Filter {

    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String token = request.getHeader("token");
        if (token != null) {
            AuthenticationManager authenticationManager =
                    (AuthenticationManager) WebApplicationContextUtils.
                            getRequiredWebApplicationContext(filterConfig.getServletContext()).
                            getBean("authenticationManager");

            TokenAuthentication tokenAuthentication = new TokenAuthentication(UUID.fromString(token));
            tokenAuthentication.setAuthenticated(false);
            //SecurityContextHolder.getContext().setAuthentication(tokenAuthentication);
            try {
                TokenAuthentication authentication = (TokenAuthentication) authenticationManager.authenticate(
                        tokenAuthentication
                );

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
