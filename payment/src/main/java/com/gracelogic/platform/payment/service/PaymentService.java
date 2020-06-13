package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.IncorrectPaymentStateException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
import com.gracelogic.platform.payment.dto.PaymentDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.dto.AuthorizedUser;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface PaymentService {
    Account checkPaymentAbility(UUID paymentSystemId, String accountNumber, String currency) throws InvalidPaymentSystemException, AccountNotFoundException;

    CalcPaymentFeeResult calcPaymentFee(PaymentSystem paymentSystem, Double registeredAmount);

    Payment processPayment(UUID paymentSystemId, ProcessPaymentRequest paymentModel, AuthorizedUser executedBy) throws PaymentAlreadyExistException, AccountNotFoundException, InvalidPaymentSystemException;

    void cancelPayment(UUID paymentId) throws AccountNotFoundException, IncorrectPaymentStateException, InsufficientFundsException;

    void restorePayment(UUID paymentId) throws AccountNotFoundException, IncorrectPaymentStateException, InsufficientFundsException;

    EntityListResponse<PaymentDTO> getPaymentsPaged(UUID userId, UUID accountId, UUID paymentSystemId, Collection<UUID> paymentStateIds, Date startDate, Date endDate, boolean enrich, boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
