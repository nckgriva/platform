package com.gracelogic.platform.user.controller;

import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.dto.UserSettingDTO;
import com.gracelogic.platform.user.model.UserSetting;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.ValueRequest;
import com.gracelogic.platform.web.service.LocaleHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 21:58
 */
@Controller
@RequestMapping(value = Path.API_SETTING)
public class SettingController extends AbstractAuthorizedController {
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUserSetting(@PathVariable(value = "key") String key) {
        if (getUser() == null) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("auth.NOT_AUTHORIZED", messageSource.getMessage("auth.NOT_AUTHORIZED", null, LocaleHolder.getLocale())), HttpStatus.UNAUTHORIZED);
        }

        UserSetting userSetting = userService.getUserSetting(getUser().getId(), key);
        if (userSetting != null) {
            return new ResponseEntity<UserSettingDTO>(UserSettingDTO.prepare(userSetting), HttpStatus.OK);
        } else {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("setting.NOT_FOUND", messageSource.getMessage("setting.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{key}/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity saveUserSetting(@PathVariable(value = "key") String key,
                                          @RequestBody ValueRequest request) {
        if (getUser() == null) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("auth.NOT_AUTHORIZED", messageSource.getMessage("auth.NOT_AUTHORIZED", null, LocaleHolder.getLocale())), HttpStatus.UNAUTHORIZED);
        }

        userService.updateUserSetting(getUser().getId(), key, request.getValue());

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }
}
