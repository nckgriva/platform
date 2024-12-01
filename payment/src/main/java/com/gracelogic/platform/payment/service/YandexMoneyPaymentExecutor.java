package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/*
 Integration with payment services like: Yandex.Money, YooMoney
 https://yoomoney.ru/
*/
public class YandexMoneyPaymentExecutor implements PaymentExecutor {
    private static Logger logger = LoggerFactory.getLogger(YandexMoneyPaymentExecutor.class);

    private static final String ACTION_CHECK = "checkOrder";
    private static final String ACTION_PAY = "paymentAviso";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String RESPONSE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
            "<%sResponse performedDatetime=\"%s\" \n" +
            "code=\"%s\" invoiceId=\"%s\" \n" +
            "shopId=\"%s\"/>";


    @Override
    public PaymentExecutionResultDTO execute(PaymentSystem paymentSystem, PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void processCallback(PaymentSystem paymentSystem, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException {
        Map<String, String> params = JsonUtils.jsonToMap(paymentSystem.getFields());

        PaymentService paymentService;

        try {
            paymentService = context.getBean(PaymentService.class);
        }
        catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        String action = request.getParameter("action");
        String account = request.getParameter("customerNumber");
        String amount = request.getParameter("orderSumAmount");
        String payId = request.getParameter("invoiceId");
        String shopId = request.getParameter("shopId");
        String hash = request.getParameter("md5");
        String payDate = request.getParameter("orderCreatedDatetime");
        String orderSumCurrencyPaycash = request.getParameter("orderSumCurrencyPaycash");
        String orderSumBankPaycash = request.getParameter("orderSumBankPaycash");
        String paymentType = request.getParameter("paymentType");


        logger.info("Yandex request");
        logger.info("action: {}", action);
        logger.info("customerNumber: {}", account);
        logger.info("orderSumAmount: {}", amount);
        logger.info("invoiceId: {}", payId);
        logger.info("shopId: {}", shopId);
        logger.info("md5: {}", hash);
        logger.info("orderSumCurrencyPaycash: {}", orderSumCurrencyPaycash);
        logger.info("paymentType: {}", paymentType);
        logger.info("orderSumBankPaycash: {}", orderSumBankPaycash);
        logger.info("orderCreatedDatetime: {}", payDate);

        String calcString = String.format("%s;%s;%s;%s;%s;%s;%s;%s", action, amount, orderSumCurrencyPaycash, orderSumBankPaycash, shopId, payId, account, params.get(PARAMETER_SECRET_KEY));

        String resp;
        if (action.equalsIgnoreCase(ACTION_CHECK)) {
            if (StringUtils.isEmpty(hash) || !DigestUtils.md5Hex(calcString).equalsIgnoreCase(hash)) {
                logger.info("MD5 INVALID");
                resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "1", payId, shopId);
            } else {
                Account result = null;
                try {
                    result = paymentService.checkPaymentAbility(paymentSystem.getId(), account, orderSumCurrencyPaycash);
                }
                catch (Exception ignored) {}

                if (result != null) {
                    resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "0", payId, shopId);
                } else {
                    resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "100", payId, shopId);
                }
            }
        } else if (action.equalsIgnoreCase(ACTION_PAY)) {
            if (StringUtils.isEmpty(hash) || !DigestUtils.md5Hex(calcString).equalsIgnoreCase(hash)) {
                logger.info("MD5 INVALID");
                resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "1", payId, shopId);
            } else {
                if (!StringUtils.equalsIgnoreCase("643", orderSumCurrencyPaycash) && !StringUtils.equalsIgnoreCase("10643", orderSumCurrencyPaycash)) {
                    logger.info("CURRENCY INVALID");
                    resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "200", payId, shopId);
                } else {
                    ProcessPaymentRequest paymentModel = new ProcessPaymentRequest();
                    paymentModel.setExternalIdentifier(account);
                    paymentModel.setPaymentUID(payId);
                    paymentModel.setRegisteredAmount(Double.parseDouble(amount));
                    paymentModel.setExternalTypeUID(paymentType);
                    paymentModel.setCurrency("RUB");

                    Payment result = null;
                    try {
                        result = paymentService.processPayment(paymentSystem.getId(), paymentModel, null);
                    } catch (Exception ignored) {
                    }

                    if (result != null) {
                        resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "0", payId, shopId);
                    } else {
                        resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "100", payId, shopId);
                    }
                }
            }
        } else {
            resp = String.format(RESPONSE_TEMPLATE, action, DATE_FORMAT.format(new Date()), "200", payId, shopId);
            logger.info("ACTION INVALID");
        }

        try {
            logger.info("Response: {}", resp);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(resp);
            response.getWriter().flush();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return false;
    }
}
