package com.gracelogic.platform.market.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.market.dto.MarketAwareObjectDTO;
import com.gracelogic.platform.market.dto.OrderDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.exception.ProductNotPurchasedException;
import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.market.model.Product;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import java.util.*;

public interface MarketService {
    Order saveOrder(OrderDTO dto, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ObjectNotFoundException, ForbiddenException, InvalidDiscountException;

    PaymentExecutionResultDTO executeOrder(UUID orderId, UUID paymentSystemId, Map<String, String> params, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ForbiddenException, InvalidPaymentSystemException, AccountNotFoundException, InsufficientFundsException, InvalidDiscountException, ObjectNotFoundException, PaymentExecutionException;

    void cancelOrder(UUID orderId) throws InvalidOrderStateException, ForbiddenException, ObjectNotFoundException, InsufficientFundsException, AccountNotFoundException;

    void processPayment(Payment payment) throws InvalidOrderStateException, AccountNotFoundException, InsufficientFundsException;

    void deleteOrder(UUID orderId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, ObjectNotFoundException, ForbiddenException;

    void checkAtLeastOneProductPurchased(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkOnDate) throws ProductNotPurchasedException;

    Map<UUID, Boolean> getProductsPurchaseState(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkDate);

    Map<UUID, Product> findProducts(Map<UUID, UUID> referenceObjectIdsAndProductTypeIds);

    void enrichMarketInfo(UUID productTypeId, Collection<MarketAwareObjectDTO> objects, UUID relatedUserId, Date checkOnDate);
}