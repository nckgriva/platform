package com.gracelogic.platform.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.*;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.model.UserSession;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import com.gracelogic.platform.web.dto.SingleValueDTO;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_USER)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_USER, tags = {"User API"},
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

    @Autowired
    private IdObjectService idObjectService;

    @ApiOperation(
            value = "login",
            notes = "User login",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 423, message = "User blocked"),
            @ApiResponse(code = 429, message = "Too many attemps"),
            @ApiResponse(code = 422, message = "Not activated"),
            @ApiResponse(code = 510, message = "Not allowed IP"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
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
            notes = "Checking user login status",
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
            value = "emailValid",
            notes = "Validate e-mail",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Invalid e-mail"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/email-valid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isEMailValid(@ApiParam(name = "singleValueDTO", value = "singleValueDTO")
                                       @RequestBody
                                               SingleValueDTO singleValueDTO) {
        if (!userService.checkEmail(singleValueDTO.getValue(), true)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_EMAIL", messageSource.getMessage("register.INVALID_EMAIL", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "phoneValid",
            notes = "Validate phone number",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Invalid phone"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/phone-valid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isPhoneValid(@ApiParam(name = "singleValueDTO", value = "singleValueDTO")
                                       @RequestBody
                                               SingleValueDTO singleValueDTO) {
        if (!userService.checkPhone(singleValueDTO.getValue(), true)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_PHONE", messageSource.getMessage("register.INVALID_PHONE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "verifyEmail",
            notes = "Verify e-mail",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Invalid code"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/verify-email", method = RequestMethod.POST)
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
            notes = "Verify phone number",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/verify-phone", method = RequestMethod.POST)
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
            notes = "Register user",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity register(@ApiParam(name = "userRegistrationDTO", value = "userRegistrationDTO")
                                   @RequestBody
                                           UserRegistrationDTO userRegistrationDTO) {
        try {
            User user = lifecycleService.register(userRegistrationDTO, false);
            return new ResponseEntity<>(new IDResponse(user.getId()), HttpStatus.OK);
        } catch (PhoneOrEmailIsNecessaryException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.PHONE_OR_EMAIL_IS_NECESSARY", messageSource.getMessage("register.PHONE_OR_EMAIL_IS_NECESSARY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidPasswordException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_PASSWORD", messageSource.getMessage("register.INVALID_PASSWORD", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidPhoneException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_PHONE", messageSource.getMessage("register.INVALID_PHONE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidEmailException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("register.INVALID_EMAIL", messageSource.getMessage("register.INVALID_EMAIL", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CustomLocalizedException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "sendRepairCode",
            notes = "Send code for repair account password",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/send-repair-code", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendRepairCode(@ApiParam(name = "request", value = "request")
                                         @RequestBody
                                                 RepairCodeRequestDTO request) {

        try {
            userService.sendRepairCode(request.getLogin(), request.getLoginType(), null);
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
            value = "changePassword",
            notes = "Change password",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changePassword(@ApiParam(name = "request", value = "request")
                                         @RequestBody
                                                 ChangePasswordRequestDTO request) {
        try {
            userService.changePassword(request.getLogin(), request.getLoginType(), request.getCode(), request.getNewPassword());
        } catch (IncorrectAuthCodeException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.AUTH_CODE_IS_INCORRECT", messageSource.getMessage("common.AUTH_CODE_IS_INCORRECT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.USER_NOT_FOUND", messageSource.getMessage("common.USER_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "getUsers",
            notes = "Change list of user with selected parameters",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('USER:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUsers(@ApiParam(name = "phone", value = "phone") @RequestParam(value = "phone", required = false) String phone,
                                   @ApiParam(name = "email", value = "email") @RequestParam(value = "email", required = false) String email,
                                   @ApiParam(name = "approved", value = "approved") @RequestParam(value = "approved", required = false) Boolean approved,
                                   @ApiParam(name = "blocked", value = "blocked") @RequestParam(value = "blocked", required = false) Boolean blocked,
                                   @ApiParam(name = "fetchRoles", value = "fetchRoles") @RequestParam(value = "fetchRoles", required = false) Boolean fetchRoles,
                                   @ApiParam(name = "start", value = "start") @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                   @ApiParam(name = "count", value = "count") @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                   @ApiParam(name = "sortField", value = "sortField") @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                   @ApiParam(name = "sortDir", value = "sortDir") @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                   @ApiParam(name = "fields", value = "fields") @RequestParam Map<String, String> allRequestParams) {

        Map<String, String> fields = new HashMap<String, String>();
        if (allRequestParams != null) {
            for (String paramName : allRequestParams.keySet()) {
                if (StringUtils.startsWithIgnoreCase(paramName, "fields.")) {
                    String value = allRequestParams.get(paramName);
                    if (!StringUtils.isEmpty(value)) {
                        fields.put(paramName.substring("fields.".length()), allRequestParams.get(paramName));
                    }
                }
            }
        }

        EntityListResponse<UserDTO> users = userService.getUsersPaged(phone, email, approved, blocked, fields, fetchRoles != null ? fetchRoles : false, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<UserDTO>>(users, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getUser",
            notes = "Get user",
            response = UserDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('USER:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getUser(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            UserDTO userDTO = userService.getUser(id, true);
            return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.USER_NOT_FOUND", messageSource.getMessage("common.USER_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveUser",
            notes = "Save user",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('USER:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveUser(@ApiParam(name = "user", value = "user") @RequestBody UserDTO userDTO) {
        try {
            User user = lifecycleService.save(userDTO, true, getUser());
            return new ResponseEntity<IDResponse>(new IDResponse(user.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.USER_NOT_FOUND", messageSource.getMessage("common.USER_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteUser",
            notes = "Delete user",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Failed to delete user", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('USER:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteUser(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            lifecycleService.delete(idObjectService.getObjectById(User.class, id));
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.FAILED_TO_DELETE_USER", messageSource.getMessage("common.FAILED_TO_DELETE_USER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
