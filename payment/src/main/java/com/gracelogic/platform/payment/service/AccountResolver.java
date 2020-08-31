package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.model.User;

import java.util.UUID;

public interface AccountResolver {
    Account getTargetAccount(UUID ownerId, String accountNumber, PaymentSystem paymentSystem, String currencyCode) throws AccountNotFoundException;

    void notifyPaymentReceived(Payment payment); //Событие поступления средств на счёт

    void notifyPaymentCancelled(Payment payment); //Событие отмены платежа

    void notifyPaymentRestored(Payment payment); //Событие восстановления отмененного ранее платежа
}
