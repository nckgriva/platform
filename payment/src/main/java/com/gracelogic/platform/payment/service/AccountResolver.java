package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.model.User;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:24
 */
public interface AccountResolver {
    Account getTargetAccount(User user, String accountNumber, PaymentSystem paymentSystem, String currency) throws AccountNotFoundException;

    void notifyPaymentReceived(Payment payment); //Событие поступления средств на счёт

    void notifyPaymentCancelled(Payment payment); //Событие отмены платежа

    void notifyPaymentRestored(Payment payment); //Событие восстановления отмененного ранее платежа
}
