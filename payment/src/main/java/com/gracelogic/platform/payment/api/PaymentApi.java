package com.gracelogic.platform.payment.api;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.IncorrectPaymentStateException;
import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.Path;
import com.gracelogic.platform.payment.dto.PaymentDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.service.PaymentService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;


/**
 * Author: Igor Parkhomenko
 * Date: 10.08.2016
 * Time: 10:15
 */
@Controller
@RequestMapping(value = Path.API_PAYMENT)
public class PaymentApi extends AbstractAuthorizedController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    @Qualifier("paymentMessageSource")
    private ResourceBundleMessageSource messageSource;

    @PreAuthorize("hasAuthority('PAYMENT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity payments(@RequestParam(value = "userId", required = false) UUID userId,
                                   @RequestParam(value = "accountId", required = false) UUID accountId,
                                   @RequestParam(value = "paymentSystemId", required = false) UUID paymentSystemId,
                                   @RequestParam(value = "paymentStateId", required = false) UUID paymentStateId,
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

        EntityListResponse<PaymentDTO> payments = paymentService.getPaymentsPaged(userId, accountId, paymentSystemId, paymentStateId != null ? Collections.singletonList(paymentStateId) : null, startDate, endDate, false, length, null, start, sortField, sortDir);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('PAYMENT:ADD')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity addPayment(@RequestBody ProcessPaymentRequest request) {
        try {
            paymentService.processPayment(DataConstants.PaymentSystems.MANUAL.getValue(), request, getUser());
            return new ResponseEntity<>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (PaymentAlreadyExistException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (InvalidPaymentSystemException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PAYMENT:CANCEL')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/cancel")
    @ResponseBody
    public ResponseEntity cancelPayment(@PathVariable(value = "id") UUID paymentId) {
        try {
            paymentService.cancelPayment(paymentId, getUser());
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IncorrectPaymentStateException e) {
            return new ResponseEntity<>(new ErrorResponse("payment.incorrectStateException",
                    messageSource.getMessage("payment.incorrectStateException", null, getUserLocale())),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('PAYMENT:CANCEL')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/restore")
    @ResponseBody
    public ResponseEntity restorePayment(@PathVariable(value = "id") UUID paymentId) {
        try {
            paymentService.restorePayment(paymentId, getUser());
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IncorrectPaymentStateException e) {
            return new ResponseEntity<>(new ErrorResponse("payment.incorrectStateException",
                    messageSource.getMessage("payment.incorrectStateException", null, getUserLocale())),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }
}
