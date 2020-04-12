package com.gracelogic.platform.user.api;


import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.PassphraseTypeDTO;
import com.gracelogic.platform.user.model.PassphraseType;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_PASSPHRASE_TYPE)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_PASSPHRASE_TYPE, tags = {"PassphraseType API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class PassphraseTypeApi {

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    @Qualifier("coreMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserService userService;

    @ApiOperation(
            value = "getPassphraseTypes",
            notes = "Get list of passphrase type",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('PASSPHRASE_TYPE:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPassphraseTypes(@RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                             @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                             @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                             @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                             @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        try {
            EntityListResponse<PassphraseTypeDTO> dto = userService.getPassphraseTypePaged(name, enrich, count, null, start, sortField, sortDir);
            return new ResponseEntity<EntityListResponse<PassphraseTypeDTO>>(dto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("FAIL_GET_PASSPHRASE_TYPE_LIST", messageSource.getMessage("FAIL_GET_PASSPHRASE_TYPE_LIST", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(
            value = "getPassphraseType",
            notes = "Get passphrase type",
            response = PropertyDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('PASSPHRASE_TYPE:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getPassphraseType(@PathVariable(value = "id") UUID id) {
        try {
            PassphraseTypeDTO dto = userService.getPassphraseType(id);
            return new ResponseEntity<PassphraseTypeDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("FAIL_GET_PASSPHRASE_TYPE", messageSource.getMessage("FAIL_GET_PASSPHRASE_TYPE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "savePassphraseType",
            notes = "Save passphrase type",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('PASSPHRASE_TYPE:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity savePassphraseType(@RequestBody PassphraseTypeDTO passphraseTypeDTO) {
        try {
            PassphraseType passphraseType = userService.savePassphraseType(passphraseTypeDTO);
            return new ResponseEntity<>(new IDResponse(passphraseType.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("FAIL_SAVE_PASSPHRASE_TYPE_LIST", messageSource.getMessage("FAIL_SAVE_PASSPHRASE_TYPE_LIST", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deletePassphraseType",
            notes = "Delete passphrase type",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('PASSPHRASE_TYPE:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deletePassphraseType(@PathVariable(value = "id") UUID id) {
        try {
            userService.deletePassphraseType(id);
            return new ResponseEntity<>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }
}
