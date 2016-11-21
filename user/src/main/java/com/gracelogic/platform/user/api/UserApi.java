package com.gracelogic.platform.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.*;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.ValueRequest;
import com.gracelogic.platform.web.service.LocaleHolder;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 21:58
 */
@Controller
@RequestMapping(value = Path.API_USER)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_USER, description = "Базовый контроллер авторизации/аутентификации",
        authorizations = @Authorization(value = "MybasicAuth"))
public class UserApi extends AbstractAuthorizedController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserLifecycleService lifecycleService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
        ObjectMapper objectMapper = new ObjectMapper();

        Exception exception = null;
        if (authRequestDTO != null) {
            AuthenticationToken authentication = null;
            try {
                authentication = (AuthenticationToken) authenticationManager.authenticate(
                        new AuthenticationToken(StringUtils.lowerCase(authRequestDTO.getLogin()), authRequestDTO.getPassword(), ServletUtils.getRemoteAddress(request), authRequestDTO.getLoginType(), false)
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

        try {
            logger.debug("Response: " + resp);
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
            notes = "Проверка залогирован ли пользователь",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
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
            value = "logout",
            notes = "Разлогиниться",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
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
            value = "emailValid",
            notes = "Проверка валидности e-mail",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/emailValid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isEMailValid(@ApiParam(name = "valueRequest", value = "valueRequest")
                                       @RequestBody
                                       ValueRequest valueRequest) {
        if (!userService.checkEmail(valueRequest.getValue(), true)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_EMAIL", messageSource.getMessage("register.INVALID_EMAIL", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "phoneValid",
            notes = "Проверка валидности телефона",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/phoneValid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isPhoneValid(@ApiParam(name = "valueRequest", value = "valueRequest")
                                       @RequestBody
                                       ValueRequest valueRequest) {
        if (!userService.checkPhone(valueRequest.getValue(), true)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_PHONE", messageSource.getMessage("register.INVALID_PHONE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "verifyEmail",
            notes = "Проверка email",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/verifyEmail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verifyEmail(@ApiParam(name = "code", value = "code", required = true)
                                      @RequestParam(value = "code", required = true)
                                      String code,
                                      @ApiParam(name = "id", value = "id", required = true)
                                      @RequestParam(value = "id", required = true)
                                      UUID id) {
        if (userService.verifyLogin(id, "email", code)) {
            if (getUser() != null) {
                getUser().setEmailVerified(true);
            }
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_CODE", messageSource.getMessage("register.INVALID_CODE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "verifyPhone",
            notes = "Проверка телефона",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/verifyPhone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verifyPhone(@ApiParam(name = "code", value = "code", required = true)
                                      @RequestParam(value = "code", required = true)
                                      String code,
                                      @ApiParam(name = "id", value = "id", required = true)
                                      @RequestParam(value = "id", required = true)
                                      UUID id) {
        if (userService.verifyLogin(id, "phone", code)) {
            if (getUser() != null) {
                getUser().setPhoneVerified(true);
            }

            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_CODE", messageSource.getMessage("register.INVALID_CODE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "register",
            notes = "Зарегистрировать пользователя",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity register(@ApiParam(name = "userRegistrationDTO", value = "userRegistrationDTO")
                                   @RequestBody
                                   UserRegistrationDTO userRegistrationDTO) {
        try {
            lifecycleService.register(userRegistrationDTO, false);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (IllegalParameterException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "sendRepairCode",
            notes = "Отправить код восстановления",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/sendRepairCode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendRepairCode(@ApiParam(name = "request", value = "request")
                                         @RequestBody
                                         RepairCodeRequestDTO request) {

        try {
            userService.sendRepairCode(request.getLogin(), request.getLoginType(), null);
        } catch (SendingException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.SENDING_ERROR", messageSource.getMessage("common.SENDING_ERROR", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (IllegalParameterException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "changePassword",
            notes = "Изменить пароль",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changePassword(@ApiParam(name = "request", value = "request")
                                         @RequestBody
                                         ChangePasswordRequestDTO request) {
        try {
            userService.changePassword(request.getLogin(), request.getLoginType(), request.getCode(), request.getNewPassword());
        } catch (IllegalParameterException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }
}
