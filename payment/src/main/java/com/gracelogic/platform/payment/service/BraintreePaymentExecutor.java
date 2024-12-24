package com.gracelogic.platform.payment.service;

import com.braintreegateway.*;
import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.PaymentSystem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/*
 Integration with payment service BrainTree
 https://www.braintreepayments.com/
*/
public class BraintreePaymentExecutor implements PaymentExecutor {
    private static final String ACTION_TOKEN = "token";
    private static final String ACTION_CHECKOUT = "checkout";
    private static final String ACTION = "action";

    private static Log logger = LogFactory.getLog(BraintreePaymentExecutor.class);

    @Override
    public PaymentExecutionResultDTO execute(PaymentSystem paymentSystem, PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        Map<String, String> params = JsonUtils.jsonToMap(paymentSystem.getFields());


        if (request.getParams() == null || !request.getParams().containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
        }

        PaymentService paymentService = null;
        try {
            paymentService = context.getBean(PaymentService.class);
        }
        catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        String action = request.getParams().get(ACTION);
        logger.info("action: %s".formatted(action));
        BraintreeGateway gateway = new BraintreeGateway(
                StringUtils.equalsIgnoreCase(params.get(PARAMETER_IS_PRODUCTION), "true") ? Environment.PRODUCTION : Environment.SANDBOX,
                params.get(PARAMETER_CLIENT_ID),
                params.get(PARAMETER_PUBLIC_KEY),
                params.get(PARAMETER_SECRET_KEY)
        );

        if (StringUtils.equalsIgnoreCase(action, ACTION_TOKEN)) {
            String token = gateway.clientToken().generate();
            logger.info("token: %s".formatted(token));
            Map<String, String> responseParams = new HashMap<>();
            responseParams.put("token", token);
            return new PaymentExecutionResultDTO(false, request.getUniquePaymentIdentifier(), responseParams);
        } else if (StringUtils.equalsIgnoreCase(action, ACTION_CHECKOUT)) {
            String nonce = request.getParams().get("nonce");
            logger.info("nonce: %s".formatted(nonce));
            if (StringUtils.isEmpty(nonce)) {
                throw new PaymentExecutionException("Invalid nonce");
            }
            double dAmount = FinanceUtils.toFractional(request.getAmount());
            TransactionRequest transactionRequest = new TransactionRequest()
                    .amount(new BigDecimal(dAmount))
                    .paymentMethodNonce(nonce)
                    .options()
                    .submitForSettlement(true)
                    .done();

            Result<Transaction> result = gateway.transaction().sale(transactionRequest);
            logger.info("Transaction result: %s; message: %s".formatted(result.isSuccess(), result.getMessage()));
            if (result.isSuccess()) {
                ProcessPaymentRequest req = new ProcessPaymentRequest();
                req.setExternalIdentifier(request.getUniquePaymentIdentifier());
                req.setRegisteredAmount(dAmount);
                req.setPaymentUID(nonce);
                try {
                    paymentService.processPayment(request.getPaymentSystemId(), req, null);
                } catch (Exception e) {
                    throw new PaymentExecutionException(e.getMessage());
                }
                return new PaymentExecutionResultDTO(true, request.getUniquePaymentIdentifier(), null);
            }
            else {
                throw new PaymentExecutionException("Payment failed");
            }
        } else {
            throw new PaymentExecutionException("Invalid action value");
        }
    }

    @Override
    public void processCallback(PaymentSystem paymentSystem, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return false;
    }
}