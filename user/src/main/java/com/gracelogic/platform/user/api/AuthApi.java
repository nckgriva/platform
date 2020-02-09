package com.gracelogic.platform.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthRequestDTO;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.ChangePasswordViaVerificationCodeRequestDTO;
import com.gracelogic.platform.user.dto.SendVerificationCodeForPasswordChangingRequestDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.SingleValueDTO;
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
@RequestMapping(value = Path.API_AUTH)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_AUTH, tags = {"Auth API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class AuthApi extends AbstractAuthorizedController {
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
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
        ObjectMapper objectMapper = new ObjectMapper();

        Exception exception = null;
        if (authRequestDTO != null) {
            AuthenticationToken authentication = null;
            try {
                authentication = (AuthenticationToken) authenticationManager.authenticate(
                        new AuthenticationToken(StringUtils.lowerCase(authRequestDTO.getIdentifierValue()), authRequestDTO.getPassword(), ServletUtils.getRemoteAddress(request), authRequestDTO.getIdentifierTypeId(), false)
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
                            HttpSession session = request.getSession(false);
                            UserSession userSession = userService.updateSessionInfo(session, authentication, request.getHeader("User-Agent"), false);
                            if (userSession != null) {
                                authorizedUser.setUserSessionId(userSession.getId());
                            }

                            lifecycleService.signIn(authorizedUser, session);
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
            value = "logged",
            notes = "Checking user processSignIn status",
            response = AuthorizedUser.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/logged", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity logged() {
        if (getUser() != null) {
            return new ResponseEntity<AuthorizedUser>(getUser(), HttpStatus.OK);
        } else {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("auth.NOT_AUTHORIZED", messageSource.getMessage("auth.NOT_AUTHORIZED", null, LocaleHolder.getLocale())), HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(
            value = "locale",
            notes = "Get current locale",
            response = SingleValueDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/locale", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity locale() {
        return new ResponseEntity<SingleValueDTO>(new SingleValueDTO(LocaleHolder.getLocale().toString()), HttpStatus.OK);

    }

    @ApiOperation(
            value = "changeLocale",
            notes = "Change current locale",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Invalid locale"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/change-locale", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changeLocale(@RequestBody SingleValueDTO valueDTO, HttpServletRequest request) {
        try {
            userService.changeLocale(request, getUser(), valueDTO.getValue());
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.INVALID_LOCALE", messageSource.getMessage("common.INVALID_LOCALE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "logout",
            notes = "Logout user",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity logout(HttpServletRequest request) {
        try {
            request.getSession(false).invalidate();
            SecurityContextHolder.clearContext();
        } catch (Exception ignored) {
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }



    @ApiOperation(
            value = "sendVerificationCodeForPasswordChanging",
            notes = "Send verificaton code for password changing",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/send-verification-code-for-password-changing", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendVerificationCodeForPasswordChanging(@ApiParam(name = "request", value = "request")
                                         @RequestBody SendVerificationCodeForPasswordChangingRequestDTO request) {

        try {
            userService.sendVerificationCodeForPasswordChanging(request.getIdentifierTypeId(), request.getIdentifierValue(), null);
        } catch (SendingException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.SENDING_ERROR", messageSource.getMessage("common.SENDING_ERROR", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (TooFastOperationException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.TOO_FAST_OPERATION", messageSource.getMessage("common.TOO_FAST_OPERATION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.USER_NOT_FOUND", messageSource.getMessage("common.USER_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "changePasswordViaVerificationCode",
            notes = "Change password via verification code",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/change-password-via-verification-code", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changePassword(@ApiParam(name = "request", value = "request")
                                         @RequestBody ChangePasswordViaVerificationCodeRequestDTO request) {
        try {
            userService.changePasswordViaVerificationCode(request.getIdentifierTypeId(), request.getIdentifierValue(), request.getVerificationCode(), request.getNewPassword());
        } catch (InvalidPassphraseException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.AUTH_CODE_IS_INCORRECT", messageSource.getMessage("common.AUTH_CODE_IS_INCORRECT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.USER_NOT_FOUND", messageSource.getMessage("common.USER_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }


}
