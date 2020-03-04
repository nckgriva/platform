package com.gracelogic.platform.user.api;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.ChangePasswordViaVerificationCodeRequestDTO;
import com.gracelogic.platform.user.dto.SendVerificationCodeForPasswordChangingRequestDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.SingleValueDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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

    @ApiOperation(
            value = "info",
            notes = "Checking user info",
            response = AuthorizedUser.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity info() {
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
