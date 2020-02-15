package com.gracelogic.platform.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthRequestDTO;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.SessionBasedAuthentication;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = Path.API_AUTH_SESSION)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_AUTH, tags = {"Session Auth API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class SessionAuthApi extends AbstractAuthorizedController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserLifecycleService lifecycleService;

    @ApiOperation(
            value = "signIn",
            notes = "Sign in",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 423, message = "User blocked"),
            @ApiResponse(code = 429, message = "Too many attempts"),
            @ApiResponse(code = 422, message = "Not activated"),
            @ApiResponse(code = 510, message = "Not allowed IP"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
        ObjectMapper objectMapper = new ObjectMapper();

        Exception exception = null;
        if (authRequestDTO != null) {
            SessionBasedAuthentication authentication = null;
            try {
                authentication = (SessionBasedAuthentication) authenticationManager.authenticate(
                        new SessionBasedAuthentication(StringUtils.lowerCase(authRequestDTO.getIdentifierValue()), authRequestDTO.getPassword(), ServletUtils.getRemoteAddress(request), authRequestDTO.getIdentifierTypeId(), false)
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
                            HttpSession session = request.getSession(true);
                            UserSession userSession = userService.updateSessionInfo(session, authentication, request.getHeader("User-Agent"), false);
                            if (userSession != null) {
                                authorizedUser.setUserSessionId(userSession.getId());
                            }

                            lifecycleService.signIn(authorizedUser);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }

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
            } else if (exception instanceof UserNotApprovedException) {
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
            } else if (exception instanceof InvalidIdentifierException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                try {
                    resp = objectMapper.writeValueAsString(new ErrorResponse("INVALID_IDENTIFIER", messageSource.getMessage("auth.INVALID_IDENTIFIER", null, LocaleHolder.getLocale())));
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

        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().print(resp);
            response.getWriter().flush();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(
            value = "signOut",
            notes = "Sign out user",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/sign-out", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity signOut(HttpServletRequest request) {
        try {
            request.getSession(false).invalidate();
            SecurityContextHolder.clearContext();
        } catch (Exception ignored) {
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

}
