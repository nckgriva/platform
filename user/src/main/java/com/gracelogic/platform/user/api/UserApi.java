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
public class UserApi extends AbstractAuthorizedController {
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    private UserLifecycleService lifecycleService;

    @Autowired
    private IdObjectService idObjectService;

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity signUp(@RequestBody SignUpDTO signUpDTO) {
        try {
            User user = lifecycleService.signUp(signUpDTO);
            return new ResponseEntity<>(new IDResponse(user.getId()), HttpStatus.OK);
        } catch (InvalidPassphraseException e) {
            return new ResponseEntity<>(new ErrorResponse("signUp.INVALID_PASSPHRASE", messageSource.getMessage("signUp.INVALID_PASSPHRASE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidIdentifierException e) {
            return new ResponseEntity<>(new ErrorResponse("signUp.INVALID_IDENTIFIER", messageSource.getMessage("signUp.INVALID_IDENTIFIER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CustomLocalizedException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), messageSource.getMessage(e.getMessage(), null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("common.UNEXPECTED_ERROR", messageSource.getMessage("common.UNEXPECTED_ERROR", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('USER:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUsers(@RequestParam(value = "identifierValue", required = false) String identifierValue,
                                   @RequestParam(value = "approved", required = false) Boolean approved,
                                   @RequestParam(value = "blocked", required = false) Boolean blocked,
                                   @RequestParam(value = "fetchRoles", required = false) Boolean fetchRoles,
                                   @RequestParam(value = "calculate", defaultValue = "false") Boolean calculate,
                                   @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                   @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                   @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                   @RequestParam Map<String, String> allRequestParams) {

        Map<String, String> fields = new HashMap<>();
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

        EntityListResponse<UserDTO> users = userService.getUsersPaged(identifierValue, approved, blocked, fields, fetchRoles != null ? fetchRoles : false, calculate, length, null, start, sortField, sortDir);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('USER:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getUser(@PathVariable(value = "id") UUID id) {
        try {
            UserDTO userDTO = userService.getUser(id, true);
            return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('USER:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveUser(@RequestBody UserDTO userDTO) {
        try {
            User user = lifecycleService.save(userDTO, true, true, getUser());
            return new ResponseEntity<>(new IDResponse(user.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidPassphraseException e) {
            return new ResponseEntity<>(new ErrorResponse("signUp.INVALID_PASSPHRASE", messageSource.getMessage("signUp.INVALID_PASSPHRASE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidIdentifierException e) {
            return new ResponseEntity<>(new ErrorResponse("signUp.INVALID_IDENTIFIER", messageSource.getMessage("signUp.INVALID_IDENTIFIER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("common.UNEXPECTED_ERROR", messageSource.getMessage("common.UNEXPECTED_ERROR", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('USER:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteUser(@PathVariable(value = "id") UUID id) {
        try {
            lifecycleService.delete(idObjectService.getObjectById(User.class, id));
            return new ResponseEntity<>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
