package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

public interface PaymentExecutor {
    PaymentExecutionResultDTO execute(PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException;

    void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException;

    boolean isRecurringPaymentsAllowed();
}