package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public interface PaymentExecutor {
    PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, Long amount, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException;
}