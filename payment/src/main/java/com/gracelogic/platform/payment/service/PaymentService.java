package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.AccountNotFoundException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.IllegalParameterException;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:38
 */
public interface PaymentService {
    Account checkPaymentAbility(PaymentSystem paymentSystem, String accountNumber, String currency);

    CalcPaymentFeeResult calcPaymentFee(PaymentSystem paymentSystem, Double registeredAmount);

    Payment processPayment(PaymentSystem paymentSystem, ProcessPaymentRequest paymentModel) throws PaymentAlreadyExistException, AccountNotFoundException;
}
