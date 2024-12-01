package com.gracelogic.platform.payment.controller;


import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.payment.service.PaymentExecutor;
import com.gracelogic.platform.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static com.gracelogic.platform.payment.Path.PLATFORM_PAYMENT;

@Controller
@RequestMapping(value = PLATFORM_PAYMENT)
public class PaymentController {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private ApplicationContext applicationContext;

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @RequestMapping(value = "/{paymentSystemId}")
    public void process(HttpServletRequest request,
                        HttpServletResponse response,
                        @PathVariable(value = "paymentSystemId") UUID paymentSystemId) throws IOException {
        logger.info("Payment callback received: {}", (paymentSystemId != null ? paymentSystemId.toString() : "null"));
        PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
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

        PaymentExecutor paymentExecutor = null;
        try {
            paymentExecutor = initializePaymentExecutor(paymentSystem.getPaymentExecutorClass());
            paymentExecutor.processCallback(paymentSystem, applicationContext, request, response);
        } catch (Exception e) {
            logger.error("Failed to process payment", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private PaymentExecutor initializePaymentExecutor(String paymentExecutorClassName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName(paymentExecutorClassName);
        return (PaymentExecutor) clazz.newInstance();
    }
}
