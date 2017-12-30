package com.gracelogic.platform.market.api;

import com.gracelogic.platform.account.dto.CurrencyDTO;
import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.CurrencyMismatchException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.exception.NoActualExchangeRateException;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.market.Path;
import com.gracelogic.platform.market.dto.ExecuteOrderRequestDTO;
import com.gracelogic.platform.market.dto.OrderDTO;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.market.service.MarketService;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_ORDER)
@Api(value = Path.API_ORDER, tags = {"Order API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class OrderApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    @Qualifier("userMessageSource")
    private ResourceBundleMessageSource userMessageSource;

    @Autowired
    @Qualifier("marketMessageSource")
    private ResourceBundleMessageSource marketMessageSource;

    @Autowired
    @Qualifier("accountMessageSource")
    private ResourceBundleMessageSource accountMessageSource;

    @Autowired
    @Qualifier("paymentMessageSource")
    private ResourceBundleMessageSource paymentMessageSource;

    @Autowired
    private MarketService marketService;

    @ApiOperation(
            value = "getOrder",
            notes = "Get Order",
            response = OrderDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Object not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getOrder(@PathVariable(value = "id") UUID id,
                                   @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                   @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts) {
        try {
            OrderDTO orderDTO = marketService.getOrder(id, enrich, withProducts, getUser());
            return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(
            value = "saveOrder",
            notes = "Save order",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveOrder(@RequestBody OrderDTO orderDTO) {
        try {
            if (getUser() == null) {
                return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
            }

            Order order = marketService.saveOrder(orderDTO, getUser());
            return new ResponseEntity<IDResponse>(new IDResponse(order.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        } catch (InvalidDiscountException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_DISCOUNT", marketMessageSource.getMessage("market.INVALID_DISCOUNT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (OrderNotConsistentException e) {
            return new ResponseEntity<>(new ErrorResponse("market.ORDER_NOT_CONSISTENT", marketMessageSource.getMessage("market.ORDER_NOT_CONSISTENT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidOrderStateException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_ORDER_STATE", marketMessageSource.getMessage("market.INVALID_ORDER_STATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (NoActualExchangeRateException e) {
            return new ResponseEntity<>(new ErrorResponse("account.NO_ACTUAL_EXCHANGE_RATE", accountMessageSource.getMessage("account.NO_ACTUAL_EXCHANGE_RATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "executeOrder",
            notes = "Execute order",
            response = PaymentExecutionResultDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.POST, value = "/execute")
    @ResponseBody
    public ResponseEntity executeOrder(@RequestBody ExecuteOrderRequestDTO executeOrderRequestDTO) {
        try {
            PaymentExecutionResultDTO response = marketService.executeOrder(executeOrderRequestDTO.getOrderId(), executeOrderRequestDTO.getPaymentSystemId(), executeOrderRequestDTO.getParams(), getUser());
            return new ResponseEntity<PaymentExecutionResultDTO>(response, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        } catch (InvalidDiscountException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_DISCOUNT", marketMessageSource.getMessage("market.INVALID_DISCOUNT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (OrderNotConsistentException e) {
            return new ResponseEntity<>(new ErrorResponse("market.ORDER_NOT_CONSISTENT", marketMessageSource.getMessage("market.ORDER_NOT_CONSISTENT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidOrderStateException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_ORDER_STATE", marketMessageSource.getMessage("market.INVALID_ORDER_STATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("account.ACCOUNT_NOT_FOUND", accountMessageSource.getMessage("account.ACCOUNT_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidPaymentSystemException e) {
            return new ResponseEntity<>(new ErrorResponse("payment.INVALID_PAYMENT_SYSTEM", paymentMessageSource.getMessage("payment.INVALID_PAYMENT_SYSTEM", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InsufficientFundsException e) {
            return new ResponseEntity<>(new ErrorResponse("account.INSUFFICIENT_FUNDS", accountMessageSource.getMessage("account.INSUFFICIENT_FUNDS", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (PaymentExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("payment.FAILED_TO_EXECUTE_PAYMENT", paymentMessageSource.getMessage("payment.FAILED_TO_EXECUTE_PAYMENT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CurrencyMismatchException e) {
            return new ResponseEntity<>(new ErrorResponse("account.CURRENCY_MISMATCH", accountMessageSource.getMessage("account.CURRENCY_MISMATCH", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "cancelOrder",
            notes = "Cancel order",
            response = PaymentExecutionResultDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('ORDER:CANCEL')")
    @RequestMapping(method = RequestMethod.POST, value = "/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity cancelOrder(@PathVariable(value = "orderId") UUID orderId) {
        try {
            marketService.cancelOrder(orderId);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.NOT_FOUND);
        } catch (InvalidOrderStateException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_ORDER_STATE", marketMessageSource.getMessage("market.INVALID_ORDER_STATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("account.ACCOUNT_NOT_FOUND", accountMessageSource.getMessage("account.ACCOUNT_NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InsufficientFundsException e) {
            return new ResponseEntity<>(new ErrorResponse("account.INSUFFICIENT_FUNDS", accountMessageSource.getMessage("account.INSUFFICIENT_FUNDS", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CurrencyMismatchException e) {
            return new ResponseEntity<>(new ErrorResponse("account.CURRENCY_MISMATCH", accountMessageSource.getMessage("account.CURRENCY_MISMATCH", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteOrder",
            notes = "Delete order",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteOrder(@PathVariable(value = "id") UUID id) {
        try {

            marketService.deleteOrder(id, getUser());
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        } catch (InvalidOrderStateException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_ORDER_STATE", marketMessageSource.getMessage("market.INVALID_ORDER_STATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getOrders",
            notes = "Get list of orders",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getOrders(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "orderStateId", required = false) UUID orderStateId,
            @RequestParam(value = "discountId", required = false) UUID discountId,
            @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
            @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
            @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        try {
            AuthorizedUser authorizedUser = getUser();
            if (authorizedUser == null || !authorizedUser.getGrants().contains("ORDER:SHOW") && (userId == null || !userId.equals(authorizedUser.getId()))) {
                throw new ForbiddenException();
            }

            EntityListResponse<OrderDTO> orders = marketService.getOrdersPaged(userId, orderStateId, discountId, enrich, withProducts, length, null, start, sortField, sortDir);
            return new ResponseEntity<EntityListResponse<OrderDTO>>(orders, HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }

    }

    @ApiOperation(
            value = "getAvailableCurrencies",
            notes = "Get list of available currencies",
            response = List.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @RequestMapping(method = RequestMethod.GET, value = "/available-currencies")
    @ResponseBody
    public ResponseEntity getAvailableCurrencies() {
        List<CurrencyDTO> currencyDTOs = marketService.getAvailableCurrencies();
        return new ResponseEntity<List<CurrencyDTO>>(currencyDTOs, HttpStatus.OK);
    }
}
