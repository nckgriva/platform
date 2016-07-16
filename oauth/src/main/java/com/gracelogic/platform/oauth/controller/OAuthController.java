package com.gracelogic.platform.oauth.controller;

import com.gracelogic.platform.oauth.OauthConstants;
import com.gracelogic.platform.oauth.Path;
import com.gracelogic.platform.oauth.service.OAuthServiceProvider;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.controller.AbstractAuthorizedController;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Igor Parkhomenko
 * Date: 21.10.2015
 * Time: 18:33
 */
@Controller
@RequestMapping(value = Path.OAUTH)
public class OAuthController extends AbstractAuthorizedController {
    @Qualifier("vk")
    @Autowired
    private OAuthServiceProvider vk;

    @Qualifier("ok")
    @Autowired
    private OAuthServiceProvider ok;

    @Qualifier("instagram")
    @Autowired
    private OAuthServiceProvider instagram;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    private static Logger logger = Logger.getLogger(OAuthController.class);


    @RequestMapping(value = "/{authProvider}", method = RequestMethod.GET)
    public void oAuth(HttpServletRequest request,
                      HttpServletResponse response,
                      @PathVariable(value = "authProvider") String authProvider,
                      @RequestParam(value = "code", required = false) String code,
                      @RequestParam(value = "fwd", required = false) String fwd) throws IOException {

        if (StringUtils.isEmpty(code)) {
            response.sendRedirect(String.format("%s/%s", propertyService.getPropertyValue("base_url"), "content/oauth-error-code"));
            return;
        }

        User user = null;

        if (authProvider.equalsIgnoreCase(OauthConstants.AuthProviderConstants.VK.name())) {
            try {
                String additionalParameters = null;
                if (fwd != null) {
                    additionalParameters = "?fwd=" + fwd;
                }
                String requestUri = vk.buildRedirectUri(additionalParameters);
                logger.info("REQUEST_URI: " + requestUri);

                user = vk.accessToken(code, requestUri);
            } catch (Exception e) {
                logger.error("Failed to process user via vk", e);
            }
        }
        else if (authProvider.equalsIgnoreCase(OauthConstants.AuthProviderConstants.OK.name())) {
            try {
                String additionalParameters = null;
                if (fwd != null) {
                    additionalParameters = "?fwd=" + fwd;
                }
                String requestUri = ok.buildRedirectUri(additionalParameters);
                logger.info("REQUEST_URI: " + requestUri);

                user = ok.accessToken(code, requestUri);
            } catch (Exception e) {
                logger.error("Failed to process user via ok", e);
            }
        }
        else if (authProvider.equalsIgnoreCase(OauthConstants.AuthProviderConstants.INSTAGRAM.name())) {
            try {
                String additionalParameters = null;
                if (fwd != null) {
                    additionalParameters = "?fwd=" + fwd;
                }
                String requestUri = instagram.buildRedirectUri(additionalParameters);
                logger.info("REQUEST_URI: " + requestUri);

                user = instagram.accessToken(code, requestUri);
            } catch (Exception e) {
                logger.error("Failed to process user via instagram", e);
            }
        }
        else {
            try {
                response.sendRedirect(String.format("%s/%s", propertyService.getPropertyValue("base_url"), "content/oauth-error"));
            } catch (Exception ignored) {
            }
        }

        if (user == null) {
            response.sendRedirect(String.format("%s/%s", propertyService.getPropertyValue("base_url"), "content/oauth-error"));
            return;
        }

        Exception exception = null;

        //login here
        AuthenticationToken authentication = null;
        try {
            authentication = (AuthenticationToken) authenticationManager.authenticate(
                    new AuthenticationToken(user.getId(), null, ServletUtils.getRemoteAddress(request), "id", true)
            );
        } catch (Exception e) {
            exception = e;
            logger.error("Failed to authenticate oauth user", e);
        }

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                if (authentication.getDetails() instanceof AuthorizedUser) {
                    AuthorizedUser authorizedUser = ((AuthorizedUser) authentication.getDetails());

                    try {
                        UserSession userSession = userService.updateSessionInfo(request.getSession(), authentication, request.getHeader("User-Agent"), false);
                        if (userSession != null) {
                            authorizedUser.setUserSessionId(userSession.getId());
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (exception != null) {
            response.sendRedirect(String.format("%s/%s", propertyService.getPropertyValue("base_url"), "content/oauth-error"));
            return;

        }

        if (StringUtils.isEmpty(fwd)) {
            fwd = propertyService.getPropertyValue("base_url");
        }

        response.sendRedirect(fwd);
    }
}
