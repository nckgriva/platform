package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.PaymentSystem;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

public interface PaymentExecutor {
    String PARAMETER_REDIRECT_URL = "redirect_url";
    String PARAMETER_FAIL_REDIRECT_URL = "fail_redirect_url";
    String PARAMETER_CLIENT_ID = "client_id";
    String PARAMETER_PUBLIC_KEY = "public_key";
    String PARAMETER_SECRET_KEY = "secret_key";
    String PARAMETER_IS_PRODUCTION = "is_production";
    String PARAMETER_IS_INCLUDE_RECEIPT = "is_include_receipt";

    PaymentExecutionResultDTO execute(PaymentSystem paymentSystem, PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException;

    void processCallback(PaymentSystem paymentSystem, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException;

    boolean isRecurringPaymentsAllowed();
}