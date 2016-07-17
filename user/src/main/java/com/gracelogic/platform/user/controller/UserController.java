package com.gracelogic.platform.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthRequest;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.NotAllowedIPException;
import com.gracelogic.platform.user.exception.TooManyAttemptsException;
import com.gracelogic.platform.user.exception.UserBlockedException;
import com.gracelogic.platform.user.exception.UserNotActivatedException;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.ValueRequest;
import com.gracelogic.platform.web.filter.LocaleHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 21:58
 */
@Controller
@RequestMapping(value = Path.API_USER)
@Secured(PlatformRole.ANONYMOUS)
public class UserController extends AbstractAuthorizedController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    //В связи с тем, что при использовании @ResponseBody/@RequestBody невозможно установить Cookie - метод авторизации не используем эти аннтоации
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("empty");   //TODO: сделать с этим что-нибудь

        ObjectMapper objectMapper = new ObjectMapper();
        AuthRequest authRequest = null;
        try {
            authRequest = objectMapper.readValue(request.getInputStream(), AuthRequest.class);

            if (authRequest != null) {
                authRequest.setLogin(StringUtils.trim(authRequest.getLogin()));
            }


        } catch (Exception ignored) {
        }

        Exception exception = null;
        if (authRequest != null) {
            AuthenticationToken authentication = null;
            try {
                authentication = (AuthenticationToken) authenticationManager.authenticate(
                        new AuthenticationToken(StringUtils.lowerCase(authRequest.getLogin()), authRequest.getPassword(), ServletUtils.getRemoteAddress(request), authRequest.getLoginType(), false)
                );
            } catch (Exception e) {
                exception = e;
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
        }
        response.setContentType("application/json");
        String resp = "{}";

        if (exception != null) {
            if (exception instanceof UserBlockedException) {
                response.setStatus(HttpStatus.LOCKED.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("USER_BLOCKED", messageSource.getMessage("auth.USER_BLOCKED", null, LocaleHolder.getLocale())));
                } catch (Exception ignored) {
                }
            } else if (exception instanceof TooManyAttemptsException) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("TOO_MANY_ATTEMPTS", messageSource.getMessage("auth.TO_MANY_ATTEMPTS", null, LocaleHolder.getLocale())));
                } catch (Exception ignored) {
                }
            } else if (exception instanceof UserNotActivatedException) {
                response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("NOT_ACTIVATED", messageSource.getMessage("auth.NOT_ACTIVATED", null, LocaleHolder.getLocale())));
                } catch (Exception ignored) {
                }
            } else if (exception instanceof NotAllowedIPException) {
                response.setStatus(HttpStatus.NOT_EXTENDED.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("NOT_ALLOWED_IP", messageSource.getMessage("auth.NOT_ALLOWED_ID", null, LocaleHolder.getLocale())));
                } catch (Exception ignored) {
                }
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("INVALID_CREDENTIALS", messageSource.getMessage("auth.INVALID_CREDENTIALS", null, LocaleHolder.getLocale())));
                } catch (Exception ignored) {
                }
            }
        }

        modelAndView.addObject("response", resp);
        return modelAndView;
    }

    @RequestMapping(value = "/logged", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isLogged() {
        if (getUser() != null) {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity logout(HttpServletRequest request) {
        request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @RequestMapping(value = "/emailValid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isEMailValid(@RequestBody ValueRequest valueRequest) {
        if (!userService.checkEmail(valueRequest.getValue(), true)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/phoneValid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isPhoneValid(@RequestBody ValueRequest valueRequest) {
        if (!userService.checkPhone(valueRequest.getValue(), true)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/verifyEmail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verifyEmail(@RequestParam(value = "code", required = true) String code,
                                      @RequestParam(value = "id", required = true) UUID id) {
        if (userService.verifyLogin(id, "email", code)) {
            if (getUser() != null) {
                getUser().setEmailVerified(true);
            }
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/verifyPhone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verifyPhone(@RequestParam(value = "code", required = true) String code,
                                      @RequestParam(value = "id", required = true) UUID id) {
        if (userService.verifyLogin(id, "phone", code)) {
            if (getUser() != null) {
                getUser().setPhoneVerified(true);
            }

            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
