package com.gracelogic.platform.payment.controller;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.Path;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.payment.service.PaymentService;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = Path.PAYMENT_SBRF)
public class SbrfController {

    private static Logger logger = Logger.getLogger(SbrfController.class);

    private static final String ACTION_CHECK = "check";
    private static final String ACTION_PAY = "payment";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");

    private static final String RESPONSE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n " +
            "<response>\n " +
            "%s" +
            "</response>\n ";

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    public void process(HttpServletRequest request,
                        HttpServletResponse response) {

        String action = null;
        String account = null;
        String amount = null;
        String payId = null;
        String payDate = null;

        Map<String, String> params = ServletUtils.getQueryMap(request.getQueryString());
        for (String name : params.keySet()) {
            if (name.equalsIgnoreCase("action")) {
                action = params.get(name);
            } else if (name.equalsIgnoreCase("account")) {
                account = params.get(name);
            } else if (name.equalsIgnoreCase("amount")) {
                amount = params.get(name);
            } else if (name.equalsIgnoreCase("pay_id")) {
                payId = params.get(name);
            } else if (name.equalsIgnoreCase("pay_date")) {
                payDate = params.get(name);
            }
        }

        logger.info("Sbrf request");
        logger.info("ACTION:" + action);
        logger.info("ACCOUNT:" + account);
        logger.info("AMOUNT:" + amount);
        logger.info("PAY_ID:" + payId);
        logger.info("PAY_DATE:" + payDate);

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

        String resp;
        if (StringUtils.equalsIgnoreCase(action, ACTION_CHECK)) {
            Account result = null;
            try {
                result = paymentService.checkPaymentAbility(paymentSystem.getId(), account, "RUR");
            } catch (Exception ignored) {
            }

            if (result != null) {
                String phone = !StringUtils.isEmpty(result.getUser().getPhone()) ? "7XXXXXX" + result.getUser().getPhone().substring(result.getUser().getPhone().length() - 4) : "";
                resp = String.format(RESPONSE_TEMPLATE, String.format("<CODE>0</CODE><MESSAGE>OK</MESSAGE><AC-COUNT_BALANCE>%s</AC-COUNT_BALANCE><PHONE>%s</PHONE>", result.getBalance(), phone));
            } else {
                resp = String.format(RESPONSE_TEMPLATE, "<CODE>3</CODE><MESSAGE>NOT FOUND</MESSAGE>");
            }
        } else if (StringUtils.equalsIgnoreCase(action, ACTION_PAY)) {
            ProcessPaymentRequest paymentModel = new ProcessPaymentRequest();
            paymentModel.setExternalIdentifier(account);
            paymentModel.setPaymentUID(payId);
            paymentModel.setRegisteredAmount(Double.parseDouble(amount));
            paymentModel.setExternalTypeUID(null);

            try {
                Payment result = paymentService.processPayment(paymentSystem.getId(), paymentModel, null);
                resp = String.format(RESPONSE_TEMPLATE, String.format("<CODE>0</CODE><MESSAGE>OK</MESSAGE><REG_DATE>%s</REG_DATE>", DATE_FORMAT.format(new Date())));

            } catch (PaymentAlreadyExistException e) {
                resp = String.format(RESPONSE_TEMPLATE, "<CODE>8</CODE><MESSAGE>PAYMENT ALREADY REGISTERED</MESSAGE>");
            } catch (AccountNotFoundException | InvalidPaymentSystemException e) {
                resp = String.format(RESPONSE_TEMPLATE, "<CODE>5</CODE><MESSAGE>BAD REQUEST</MESSAGE>");
            }
        } else {
            resp = String.format(RESPONSE_TEMPLATE, "<CODE>2</CODE><MESSAGE>NOT FOUND</MESSAGE>");
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
