package com.gracelogic.platform.user.security;

import com.gracelogic.platform.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Author: Igor Parkhomenko
 * Date: 09.04.2015
 * Time: 23:12
 */
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
