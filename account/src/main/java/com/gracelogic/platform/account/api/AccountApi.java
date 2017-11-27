package com.gracelogic.platform.account.api;

import com.gracelogic.platform.account.Path;
import com.gracelogic.platform.account.dto.AccountDTO;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_ACCOUNT)
@Api(value = Path.API_ACCOUNT, tags = {"Account API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class AccountApi extends AbstractAuthorizedController {

    @Autowired
    private AccountService accountService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "getAccounts",
            notes = "Get list of accounts",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ACCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getAccounts( @RequestParam(value = "accountTypeId", required = false) UUID accountTypeId,
                                       @RequestParam(value = "accountCurrencyId", required = false) UUID accountCurrencyId,
                                       @RequestParam(value = "userId", required = false) UUID userId,
                                       @RequestParam(value = "externalIdentifier", required = false) String externalIdentifier,
                                       @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                       @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                       @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                       @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                       @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<AccountDTO> docs = accountService.getAccountsPaged(accountTypeId, accountCurrencyId, userId, externalIdentifier, enrich, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<AccountDTO>>(docs, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getAccount",
            notes = "Get account",
            response = AccountDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Object not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ACCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getAccount(@PathVariable(value = "id") UUID id,
                                      @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich) {
        try {
            AccountDTO dto = accountService.getAccount(id, enrich);
            return new ResponseEntity<AccountDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteAccount",
            notes = "Delete account",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Failed to delete document", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ACCOUNT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteAccount(@PathVariable(value = "id") UUID id) {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
