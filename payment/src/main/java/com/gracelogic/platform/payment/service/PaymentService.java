package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.IncorrectPaymentStateException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.dto.AuthorizedUser;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:38
 */
public interface PaymentService {
    Account checkPaymentAbility(UUID paymentSystemId, String accountNumber, String currency) throws InvalidPaymentSystemException, AccountNotFoundException;

    CalcPaymentFeeResult calcPaymentFee(PaymentSystem paymentSystem, Double registeredAmount);

    Payment processPayment(UUID paymentSystemId, ProcessPaymentRequest paymentModel, AuthorizedUser executedBy) throws PaymentAlreadyExistException, AccountNotFoundException, InvalidPaymentSystemException;

    void cancelPayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException;

    void restorePayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException;
}
