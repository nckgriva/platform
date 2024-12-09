package com.gracelogic.platform.account.api;

import com.gracelogic.platform.account.Path;
import com.gracelogic.platform.account.dto.AccountDTO;
import com.gracelogic.platform.account.dto.CalculateExchangeRequestDTO;
import com.gracelogic.platform.account.dto.CalculateExchangeResponseDTO;
import com.gracelogic.platform.account.exception.NoActualExchangeRateException;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
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
public class AccountApi extends AbstractAuthorizedController {

    @Autowired
    private AccountService accountService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    @Qualifier("accountMessageSource")
    private ResourceBundleMessageSource accountMessageSource;

    @PreAuthorize("hasAuthority('ACCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getAccounts( @RequestParam(value = "accountTypeId", required = false) UUID accountTypeId,
                                       @RequestParam(value = "currencyId", required = false) UUID currencyId,
                                       @RequestParam(value = "ownerId", required = false) UUID ownerId,
                                       @RequestParam(value = "externalIdentifier", required = false) String externalIdentifier,
                                       @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                       @RequestParam(value = "calculate", defaultValue = "false") Boolean calculate,
                                       @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                       @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                       @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                       @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<AccountDTO> docs = accountService.getAccountsPaged(accountTypeId, currencyId, ownerId, externalIdentifier, enrich, calculate, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<AccountDTO>>(docs, HttpStatus.OK);
    }

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

    @RequestMapping(method = RequestMethod.POST, value = "/calculate-exchange")
    @ResponseBody
    public ResponseEntity calculateExchange(@RequestBody CalculateExchangeRequestDTO requestDTO) {
        try {
            Long value = accountService.translateAmountInOtherCurrency(requestDTO.getSourceCurrencyId(), requestDTO.getValue(), requestDTO.getDestinationCurrencyId());
            return new ResponseEntity<CalculateExchangeResponseDTO>(new CalculateExchangeResponseDTO(value), HttpStatus.OK);
        } catch (NoActualExchangeRateException e) {
            return new ResponseEntity<>(new ErrorResponse("account.NO_ACTUAL_EXCHANGE_RATE", accountMessageSource.getMessage("account.NO_ACTUAL_EXCHANGE_RATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
