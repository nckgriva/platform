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
import com.gracelogic.platform.market.exception.*;
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

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getOrder(@PathVariable(value = "id") UUID id,
                                   @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                   @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts) {
        try {
            OrderDTO orderDTO = marketService.getOrder(id, enrich, withProducts, getUser(), false);
            return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveOrder(@RequestBody OrderDTO orderDTO) {
        try {
            if (getUser() == null) {
                return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
            }

            Order order = marketService.saveOrder(orderDTO, getUser(), false);
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
        } catch (ProductSubscriptionException e) {
            return new ResponseEntity<>(new ErrorResponse("market.PRODUCT_SUBSCRIPTION", marketMessageSource.getMessage("market.PRODUCT_SUBSCRIPTION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidProductException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_PRODUCT", marketMessageSource.getMessage("market.INVALID_PRODUCT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (EmptyOrderException e) {
            return new ResponseEntity<>(new ErrorResponse("market.EMPTY_ORDER", marketMessageSource.getMessage("market.EMPTY_ORDER", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (InvalidCurrencyException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_CURRENCY", marketMessageSource.getMessage("market.INVALID_CURRENCY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/execute")
    @ResponseBody
    public ResponseEntity executeOrder(@RequestBody ExecuteOrderRequestDTO executeOrderRequestDTO) {
        try {
            PaymentExecutionResultDTO response = marketService.executeOrder(executeOrderRequestDTO.getOrderId(), executeOrderRequestDTO.getPaymentSystemId(), executeOrderRequestDTO.getParams(), getUser(), false);
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

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteOrder(@PathVariable(value = "id") UUID id) {
        try {

            marketService.deleteOrder(id, getUser(), false);
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

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/cancel-subscription")
    @ResponseBody
    public ResponseEntity cancelSubscription(@PathVariable(value = "id") UUID id) {
        try {
            marketService.cancelSubscription(id, getUser(), false);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        } catch (InvalidOrderStateException e) {
            return new ResponseEntity<>(new ErrorResponse("market.INVALID_ORDER_STATE", marketMessageSource.getMessage("market.INVALID_ORDER_STATE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ProductSubscriptionException e) {
            return new ResponseEntity<>(new ErrorResponse("market.PRODUCT_SUBSCRIPTION", marketMessageSource.getMessage("market.PRODUCT_SUBSCRIPTION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getOrders(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "ownerId", required = false) UUID ownerId,
            @RequestParam(value = "orderStateId", required = false) UUID orderStateId,
            @RequestParam(value = "discountId", required = false) UUID discountId,
            @RequestParam(value = "totalAmountGreatThan", required = false) Double totalAmountGreatThan,
            @RequestParam(value = "onlyEmptyParentOrder", required = false, defaultValue = "false") Boolean onlyEmptyParentOrder,
            @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
            @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
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

            EntityListResponse<OrderDTO> orders = marketService.getOrdersPaged(userId, ownerId, orderStateId, discountId, totalAmountGreatThan, onlyEmptyParentOrder, enrich, calculate, withProducts, length, null, start, sortField, sortDir);
            return new ResponseEntity<EntityListResponse<OrderDTO>>(orders, HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(new ErrorResponse("auth.FORBIDDEN", userMessageSource.getMessage("auth.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/available-currencies")
    @ResponseBody
    public ResponseEntity getAvailableCurrencies() {
        List<CurrencyDTO> currencyDTOs = marketService.getAvailableCurrencies();
        return new ResponseEntity<List<CurrencyDTO>>(currencyDTOs, HttpStatus.OK);
    }
}
