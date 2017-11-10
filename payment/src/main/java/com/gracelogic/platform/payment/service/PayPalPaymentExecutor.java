package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class PayPalPaymentExecutor implements PaymentExecutor {
    @Override
    public PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, Long amount, ApplicationContext context, Map<String, String> params) {
        return new PaymentExecutionResultDTO(true, null);
    }
}