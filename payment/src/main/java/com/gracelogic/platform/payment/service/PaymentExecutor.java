package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.UUID;

public interface PaymentExecutor {
    PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, UUID paymentSystemId, Long amount, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException;
}