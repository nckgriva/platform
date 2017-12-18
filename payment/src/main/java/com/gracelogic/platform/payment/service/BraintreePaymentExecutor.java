package com.gracelogic.platform.payment.service;

import com.braintreegateway.*;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BraintreePaymentExecutor implements PaymentExecutor {
    private static final String ACTION_TOKEN = "token";
    private static final String ACTION_CHECKOUT = "checkout";
    private static final String ACTION = "action";

    private static Logger logger = Logger.getLogger(BraintreePaymentExecutor.class);

    @Override
    public PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, UUID paymentSystemId, Long amount, String currencyCode, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException {
        if (params == null || !params.containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
        }

        PropertyService propertyService = null;
        PaymentService paymentService = null;
        try {
            propertyService = context.getBean(PropertyService.class);
            paymentService = context.getBean(PaymentService.class);
        }
        catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        String action = params.get(ACTION);
        logger.info("action: " + action);
        BraintreeGateway gateway = new BraintreeGateway(
                propertyService.getPropertyValueAsBoolean("payment:braintree_is_production") ? Environment.PRODUCTION : Environment.SANDBOX,
                propertyService.getPropertyValue("payment:braintree_merchant_id"),
                propertyService.getPropertyValue("payment:braintree_public_key"),
                propertyService.getPropertyValue("payment:braintree_private_key")
        );

        if (StringUtils.equalsIgnoreCase(action, ACTION_TOKEN)) {
            String token = gateway.clientToken().generate();
            logger.info("token: " + token);
            Map<String, String> responseParams = new HashMap<>();
            responseParams.put("token", token);
            return new PaymentExecutionResultDTO(false, uniquePaymentIdentifier, responseParams);
        } else if (StringUtils.equalsIgnoreCase(action, ACTION_CHECKOUT)) {
            String nonce = params.get("nonce");
            logger.info("nonce: " + nonce);
            if (StringUtils.isEmpty(nonce)) {
                throw new PaymentExecutionException("Invalid nonce");
            }
            double dAmount = FinanceUtils.toFractional(amount);
            TransactionRequest request = new TransactionRequest()
                    .amount(new BigDecimal(dAmount))
                    .paymentMethodNonce(nonce)
                    .options()
                    .submitForSettlement(true)
                    .done();

            Result<Transaction> result = gateway.transaction().sale(request);
            logger.info("Transaction result: " + result.isSuccess() + "; message: " + result.getMessage());
            if (result.isSuccess()) {
                ProcessPaymentRequest req = new ProcessPaymentRequest();
                req.setExternalIdentifier(uniquePaymentIdentifier);
                req.setRegisteredAmount(dAmount);
                req.setPaymentUID(nonce);
                try {
                    paymentService.processPayment(paymentSystemId, req, null);
                } catch (Exception e) {
                    throw new PaymentExecutionException(e.getMessage());
                }
                return new PaymentExecutionResultDTO(true, uniquePaymentIdentifier, null);
            }
            else {
                throw new PaymentExecutionException("Payment failed");
            }
        } else {
            throw new PaymentExecutionException("Invalid action value");
        }
    }

    @Override
    public void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException("Not implemented");
    }
}