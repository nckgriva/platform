package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

//TODO: Проверить безопасность этого способа оплаты
public class SbrfPaymentExecutor implements PaymentExecutor {
    private static Logger logger = Logger.getLogger(SbrfPaymentExecutor.class);

    private static final String ACTION_CHECK = "check";
    private static final String ACTION_PAY = "payment";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");

    private static final String RESPONSE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n " +
            "<response>\n " +
            "%s" +
            "</response>\n ";

    @Override
    public PaymentExecutionResultDTO execute(PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException {
        PaymentService paymentService = null;
        try {
            paymentService = context.getBean(PaymentService.class);
        }
        catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

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

        String resp;
        if (StringUtils.equalsIgnoreCase(action, ACTION_CHECK)) {
            Account result = null;
            try {
                result = paymentService.checkPaymentAbility(paymentSystemId, account, "RUR");
            } catch (Exception ignored) {
            }

            if (result != null) {
                //String phone = !StringUtils.isEmpty(result.getUser().getPhone()) ? "7XXXXXX" + result.getUser().getPhone().substring(result.getUser().getPhone().length() - 4) : "";
                String phone = ""; //Убрано в связи с переходом на platform 1.2
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
                Payment result = paymentService.processPayment(paymentSystemId, paymentModel, null);
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

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return false;
    }
}
