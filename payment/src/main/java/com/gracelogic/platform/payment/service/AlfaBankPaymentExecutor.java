package com.gracelogic.platform.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.dto.alfabank.*;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.PaymentSystem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class AlfaBankPaymentExecutor implements PaymentExecutor {

    private static final String CONFIRMATION_URL = "confirmation_url";
    private static final String PARAMETER_LANGUAGE = "language";
    private static final String PARAMETER_PAGE_VIEW = "page_view";
    private static final String PARAMETER_CUSTOMER_EMAIL = "customer_email";
    private static final String JSON_PARAM_ORDER_ID = "orderId";

    private static final Logger logger = LoggerFactory.getLogger(AlfaBankPaymentExecutor.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public PaymentExecutionResultDTO execute(PaymentSystem paymentSystem, PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        Map<String, String> paymentSystemFields = JsonUtils.jsonToMap(paymentSystem.getFields());

        AlfaBankRegisterOrderDTO registerOrderDTO = new AlfaBankRegisterOrderDTO();
        registerOrderDTO.setToken(paymentSystemFields.get("token"));

        AlfaBankOrderNumberService orderNumberService;
        try {
            orderNumberService = context.getBean(AlfaBankOrderNumberService.class);
        } catch (Exception e) {
            throw new PaymentExecutionException("AlfaBankOrderNumberServiceImpl not found in project");
        }
        String orderNumber = paymentSystemFields.get("order_prefix") + orderNumberService.getOrderNumber();
        registerOrderDTO.setOrderNumber(orderNumber);
        Double amount = FinanceUtils.toFractional2Rounded(request.getAmount());
        registerOrderDTO.setAmount((long)(amount * 100));
        registerOrderDTO.setCurrency(getCurrencyCodeAsIso4217(request.getCurrencyCode()));

        String returnUrl = request.getParams().containsKey(PARAMETER_REDIRECT_URL) ? request.getParams().get(PARAMETER_REDIRECT_URL) : null;
        if (StringUtils.isBlank(returnUrl)) throw new PaymentExecutionException(String.format("Parameter %s is invalid or not found", PARAMETER_REDIRECT_URL));
        registerOrderDTO.setReturnUrl(returnUrl);

        String failUrl = request.getParams().containsKey(PARAMETER_FAIL_REDIRECT_URL) ? request.getParams().get(PARAMETER_FAIL_REDIRECT_URL) : null;
        registerOrderDTO.setFailUrl(failUrl);

        //registerOrderDTO.setDescription(request.getDescription());
        //registerOrderDTO.setIp("");

        registerOrderDTO.setJsonParams(String.format("{ \"%s\": \"%s\" }", JSON_PARAM_ORDER_ID, request.getUniquePaymentIdentifier())); // same as order id

        String language = request.getParams().containsKey(PARAMETER_LANGUAGE) ? request.getParams().get(PARAMETER_LANGUAGE) : null;
        registerOrderDTO.setLanguage(language); // ISO 639-1

        String pageView = request.getParams().containsKey(PARAMETER_PAGE_VIEW) ? request.getParams().get(PARAMETER_PAGE_VIEW) : null;
        registerOrderDTO.setPageView(pageView);

        registerOrderDTO.setClientId(request.getAuthorizedUserId().toString());
        String customerEmail = request.getParams().containsKey(PARAMETER_CUSTOMER_EMAIL) ? request.getParams().get(PARAMETER_CUSTOMER_EMAIL) : null;
        registerOrderDTO.setEmail(customerEmail);

        //registerOrderDTO.setPostAddress("");

        try {
            AlfaBankRegisterOrderResponseDTO response = registerOrder(registerOrderDTO, paymentSystemFields.get("endpoint"));
            if (response.getOrderId() == null) {
                throw new PaymentExecutionException(response.getErrorMessage());
            }

            HashMap<String, String> params = new HashMap<>();
            params.put(CONFIRMATION_URL, response.getFormUrl());

            return new PaymentExecutionResultDTO(false, response.getOrderId(), params);
        } catch (Exception e) {
            logger.error("Failed to execute payment with AlfaBank", e);
            throw new PaymentExecutionException(e.getMessage());
        }
    }

    @Override
    public void processCallback(PaymentSystem paymentSystem, ApplicationContext context,
                                HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException {
        logger.info("AlfaBank callback accepted");

        Map<String, String> paymentSystemFields = JsonUtils.jsonToMap(paymentSystem.getFields());
        PaymentService paymentService;
        try {
            paymentService = context.getBean(PaymentService.class);
        } catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        try {
            logger.info("raw request: {}", request.getQueryString());
            if (StringUtils.equalsIgnoreCase(request.getParameter("operation"), "deposited")) {
                AlfaBankOrderStatusRequestDTO statusRequest = new AlfaBankOrderStatusRequestDTO();
                statusRequest.setOrderId(request.getParameter("mdOrder"));
                statusRequest.setToken(paymentSystemFields.get("token"));

                AlfaBankOrderStatusResponseDTO orderStatus =
                        getOrderStatus(statusRequest, paymentSystemFields.get("endpoint"));
                logger.info(orderStatus.toString());

                if (orderStatus.getOrderStatus() != null && orderStatus.getOrderStatus() == 2) {
                    ProcessPaymentRequest req = new ProcessPaymentRequest();
                    req.setExternalIdentifier(statusRequest.getOrderId());
                    req.setRegisteredAmount(orderStatus.getAmount() / 100.0d);
                    req.setPaymentUID(orderStatus.getOrderNumber());
                    req.setCurrency(getCurrencyCodeFromIso4217(orderStatus.getCurrency()));
                    paymentService.processPayment(paymentSystem.getId(), req, null);
                } else {
                    logger.warn("Payment not succeed, but event accepted: {}", orderStatus.getOrderNumber());
                }
            } else {
                logger.info("ignoring callback");
            }

        } catch (Exception e) {
            logger.error("Failed to process AlfaBank callback", e);
            throw new PaymentExecutionException(e.getMessage());
        }
    }

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return true;
    }

    private static AlfaBankRegisterOrderResponseDTO registerOrder(AlfaBankRegisterOrderDTO request, String endpoint) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        URIBuilder uriBuilder = new URIBuilder(endpoint + "/register.do");
        uriBuilder.setParameters(request.getNameValuePairs());

        HttpPost sendMethod = new HttpPost(uriBuilder.build());
        sendMethod.addHeader("Accept", "application/json");

        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: {}", result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: {}", response);
        return mapper.readValue(response, AlfaBankRegisterOrderResponseDTO.class);
    }

    private static AlfaBankOrderStatusResponseDTO getOrderStatus(AlfaBankOrderStatusRequestDTO request, String endpoint) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        URIBuilder uriBuilder = new URIBuilder(endpoint + "/getOrderStatusExtended.do");
        uriBuilder.setParameters(request.getNameValuePairs());

        HttpPost sendMethod = new HttpPost(uriBuilder.build());
        sendMethod.addHeader("Accept", "application/json");

        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: {}", result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: {}", response);
        return mapper.readValue(response, AlfaBankOrderStatusResponseDTO.class);
    }

    private static Integer getCurrencyCodeAsIso4217(String code) throws PaymentExecutionException {
        switch (code) {
            case "RUB": return 643;
            case "USD": return 840;
            case "EUR": return 978;
        }

        throw new PaymentExecutionException("Unknown currency code");
    }

    private static String getCurrencyCodeFromIso4217(String code) throws PaymentExecutionException {
        switch (code) {
            case "643": return "RUB";
            case "840": return "USD";
            case "978": return "EUR";
        }

        throw new PaymentExecutionException("Unknown currency code");
    }
}
