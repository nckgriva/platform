package com.gracelogic.platform.market.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.market.dto.OrderDTO;
import com.gracelogic.platform.market.dto.OrderExecutionParametersDTO;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface MarketService {
    Order saveOrder(OrderDTO dto, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ObjectNotFoundException, ForbiddenException, InvalidDiscountException;

    OrderExecutionParametersDTO executeOrder(UUID orderId, UUID paymentSystemId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ForbiddenException, InvalidPaymentSystemException, AccountNotFoundException, InsufficientFundsException, InvalidDiscountException, ObjectNotFoundException;

    void cancelOrder(UUID orderId) throws InvalidOrderStateException, ForbiddenException, ObjectNotFoundException, InsufficientFundsException, AccountNotFoundException;

    void processPayment(Payment payment) throws InvalidOrderStateException, AccountNotFoundException, InsufficientFundsException;

    void deleteOrder(UUID orderId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, ObjectNotFoundException, ForbiddenException;

    boolean checkAtLeastOneProductPurchased(UUID userId, Map<UUID, UUID> referenceObjectIds, Date checkOnDate);
}
