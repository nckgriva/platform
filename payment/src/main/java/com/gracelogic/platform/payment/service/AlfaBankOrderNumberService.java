package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.exception.PaymentExecutionException;

public interface AlfaBankOrderNumberService {
    String getOrderNumber() throws PaymentExecutionException;
}
