package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.Payment;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

public interface PaymentExecutor {
    PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, UUID paymentSystemId, Long amount, String currencyCode, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException;

    void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException;
}