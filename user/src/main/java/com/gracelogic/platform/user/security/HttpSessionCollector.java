package com.gracelogic.platform.user.security;

import com.gracelogic.platform.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class HttpSessionCollector implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
        if (event.getSession() != null) {
            //updateSessionInfo(event.getSession(), false);
        }
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        if (event.getSession() != null) {
            updateSessionInfo(event.getSession(), true);
        }
    }

    private static void updateSessionInfo(HttpSession httpSession, boolean isDestroying) {
        ApplicationContext ctx =
                WebApplicationContextUtils.
                        getWebApplicationContext(httpSession.getServletContext());

        UserService userService = (UserService) ctx.getBean("userService");

        userService.updateSessionInfo(httpSession, null, null, isDestroying);
    }
}
