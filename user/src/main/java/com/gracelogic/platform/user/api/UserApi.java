package com.gracelogic.platform.user.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.SignUpDTO;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.exception.CustomLocalizedException;
import com.gracelogic.platform.user.exception.InvalidIdentifierException;
import com.gracelogic.platform.user.exception.InvalidPassphraseException;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserLifecycleService lifecycleService;

    @Autowired
    private IdObjectService idObjectService;

    @ApiOperation(
            value = "signUp",
            notes = "Sign up",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity signUp(@ApiParam(name = "signUpDTO", value = "signUpDTO")
                                 @RequestBody SignUpDTO signUpDTO) {
        try {
            User user = lifecycleService.signUp(signUpDTO);
            return new ResponseEntity<>(new IDResponse(user.getId()), HttpStatus.OK);
        } catch (InvalidPassphraseException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("signUp.INVALID_PASSPHRASE", messageSource.getMessage("signUp.INVALID_PASSPHRASE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidIdentifierException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("signUp.INVALID_IDENTIFIER", messageSource.getMessage("signUp.INVALID_IDENTIFIER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CustomLocalizedException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity getUsers(@ApiParam(name = "identifierValue", value = "identifierValue") @RequestParam(value = "identifierValue", required = false) String identifierValue,
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
                    String value = null;
                    try {
                        value = URLDecoder.decode(allRequestParams.get(paramName), "UTF-8");
                    } catch (UnsupportedEncodingException ignored) {
                    }

                    if (!StringUtils.isEmpty(value)) {
                        fields.put(paramName.substring("fields.".length()), value);
                    }
                }
            }
        }

        EntityListResponse<UserDTO> users = userService.getUsersPaged(identifierValue, approved, blocked, fields, fetchRoles != null ? fetchRoles : false, length, null, start, sortField, sortDir);
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
            User user = lifecycleService.save(userDTO, true, true, getUser());
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
