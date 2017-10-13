package com.gracelogic.platform.account.api;


import com.gracelogic.platform.account.Path;
import com.gracelogic.platform.account.dto.TransactionDTO;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_TRANSACTION)
@Api(value = Path.API_TRANSACTION, tags = {"Transaction API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class TransactionApi extends AbstractAuthorizedController {
    @Autowired
    private AccountService accountService;

    @ApiOperation(
            value = "transactions",
            notes = "Get list of transactions, enrich must be true",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Something exceptional happened", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('TRANSACTION:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity transactions(@RequestParam(value = "userId", required = false) UUID userId,
                                       @RequestParam(value = "accountId", required = false) UUID accountId,
                                       @RequestParam(value = "transactionTypeId", required = false) UUID transactionTypeId,
                                       @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                       @RequestParam(value = "startDate", required = false) String sStartDate,
                                       @RequestParam(value = "endDate", required = false) String sEndDate,
                                       @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                       @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                       @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                       @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        Date startDate = null;
        Date endDate = null;

        try {
            if (!StringUtils.isEmpty(sStartDate)) {
                startDate = DateFormatConstants.DEFAULT_DATE_FORMAT.get().parse(sStartDate);
            }
            if (!StringUtils.isEmpty(sEndDate)) {
                endDate = DateFormatConstants.DEFAULT_DATE_FORMAT.get().parse(sEndDate);
            }
        } catch (Exception ignored) {
        }

        EntityListResponse<TransactionDTO> transactions = accountService.getTransactionsPaged(userId, accountId, transactionTypeId != null ? Collections.singletonList(transactionTypeId) : null, startDate, endDate, enrich, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<TransactionDTO>>(transactions, HttpStatus.OK);
    }
}
