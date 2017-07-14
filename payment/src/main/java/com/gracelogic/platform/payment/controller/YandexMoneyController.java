package com.gracelogic.platform.payment.controller;

import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.Path;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.payment.service.PaymentService;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value = Path.PAYMENT_YANDEX_MONEY)
public class YandexMoneyController {
    private static Logger logger = Logger.getLogger(YandexMoneyController.class);

    private static final String ACTION_CHECK = "checkOrder";
    private static final String ACTION_PAY = "paymentAviso";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String RESPONSE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
            "<%sResponse performedDatetime=\"%s\" \n" +
            "code=\"%s\" invoiceId=\"%s\" \n" +
            "shopId=\"%s\"/>";

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private IdObjectService idObjectService;

    @RequestMapping(method = RequestMethod.POST, value = {"/", "check", "aviso"})
    public void getPage(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam(value = "action", required = true) String action,
                        @RequestParam(value = "customerNumber", required = true) String account,
                        @RequestParam(value = "orderSumAmount", required = false) String amount,
                        @RequestParam(value = "invoiceId", required = false) String payId,
                        @RequestParam(value = "orderCreatedDatetime", required = false) String payDate,
                        @RequestParam(value = "md5", required = false) String hash,
                        @RequestParam(value = "shopId", required = false) String shopId,
                        @RequestParam(value = "orderSumCurrencyPaycash", required = false) String orderSumCurrencyPaycash,
                        @RequestParam(value = "orderSumBankPaycash", required = false) String orderSumBankPaycash,
                        @RequestParam(value = "paymentType", required = false) String paymentType) {

        logger.info("Yandex request");
        logger.info("action:" + action);
        logger.info("customerNumber:" + account);
        logger.info("orderSumAmount:" + amount);
        logger.info("invoiceId:" + payId);
        logger.info("shopId:" + shopId);
        logger.info("md5:" + hash);
        logger.info("orderSumCurrencyPaycash:" + orderSumCurrencyPaycash);
        logger.info("paymentType:" + paymentType);
        logger.info("orderSumBankPaycash:" + orderSumBankPaycash);
        logger.info("orderCreatedDatetime:" + payDate);

        PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, DataConstants.PaymentSystems.YANDEX_MONEY.getValue());

        if (paymentSystem == null || !paymentSystem.getActive()) {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
            }
            return;
        }

        if (!StringUtils.isEmpty(paymentSystem.getAllowedAddresses()) && !StringUtils.contains(paymentSystem.getAllowedAddresses(), ServletUtils.getRemoteAddress(request))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
            }
            return;
        }

        String calcString = String.format("%s;%s;%s;%s;%s;%s;%s;%s", action, amount, orderSumCurrencyPaycash, orderSumBankPaycash, shopId, payId, account, paymentSystem.getSecurityKey());

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
                    paymentModel.setAccountNumber(account);
                    paymentModel.setPaymentUID(payId);
                    paymentModel.setRegisteredAmount(Double.parseDouble(amount));
                    paymentModel.setExternalTypeUID(paymentType);

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
            logger.info("Response: " + resp);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(resp);
            response.getWriter().flush();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
