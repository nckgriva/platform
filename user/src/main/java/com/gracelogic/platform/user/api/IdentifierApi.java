package com.gracelogic.platform.user.api;

import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.dto.IdentifierRequestDTO;
import com.gracelogic.platform.user.dto.VerifyIdentifierRequestDTO;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = Path.API_IDENTIFIER)
public class IdentifierApi extends AbstractAuthorizedController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource messageSource;

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity validate(
            @RequestBody IdentifierRequestDTO dto,
            @RequestParam(value = "checkAvailability", required = false, defaultValue = "false") Boolean checkAvailability
    ) {
        if (!userService.isIdentifierValid(dto.getIdentifierTypeId(), dto.getIdentifierValue(), checkAvailability)) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("signUp.INVALID_IDENTIFIER", messageSource.getMessage("signUp.INVALID_IDENTIFIER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verify(@RequestBody VerifyIdentifierRequestDTO dto) {
        if (userService.processIdentifierVerificationViaVerificationCode(dto.getIdentifierTypeId(), dto.getIdentifierValue(), dto.getVerificationCode())) {
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } else {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("common.AUTH_CODE_IS_INCORRECT", messageSource.getMessage("common.AUTH_CODE_IS_INCORRECT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: Написать методы по добавлению/редактированию/удалению/получению списка Identifiers


}
