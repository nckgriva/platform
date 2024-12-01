package com.gracelogic.platform.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.Utils;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.dto.yandex.kassa.*;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/*
 Integration with payment services like: Yandex.Kassa, YooKassa
 https://yookassa.ru
*/
public class YandexKassaPaymentExecutor implements PaymentExecutor {
    private static final String API_URL = "https://api.yookassa.ru";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(YandexKassaPaymentExecutor.class);

    @Override
    public PaymentExecutionResultDTO execute(PaymentSystem paymentSystem, PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        Map<String, String> params = JsonUtils.jsonToMap(paymentSystem.getFields());

        YandexKassaCreatePaymentDTO paymentDTO = new YandexKassaCreatePaymentDTO();
        paymentDTO.setAmount(new YandexKassaAmountDTO(FinanceUtils.toFractional2Rounded(request.getAmount()), request.getCurrencyCode()));
        paymentDTO.setCapture(true);
        String paymentToken = request.getParams().get("payment_token");
        if (!StringUtils.isEmpty(paymentToken)) {
            //Payment was created on the client (e.g. mobile application)
            paymentDTO.setPayment_token(paymentToken);
        }
        else {
            //Create payment on the back-end
            paymentDTO.setConfirmation(new YandexKassaConfirmationDTO("redirect", params.get(PARAMETER_REDIRECT_URL)));
        }

        if (!StringUtils.isEmpty(request.getDescription()) && StringUtils.equalsIgnoreCase(params.get(PARAMETER_IS_INCLUDE_RECEIPT), "true")) {
            YandexKassaReceiptDTO receiptDTO = new YandexKassaReceiptDTO();
            paymentDTO.setReceipt(receiptDTO);

            YandexKassaReceiptItemDTO itemDTO = new YandexKassaReceiptItemDTO();
            itemDTO.setAmount(paymentDTO.getAmount());
            itemDTO.setQuantity(1D);
            itemDTO.setDescription(request.getDescription());
            receiptDTO.getItems().add(itemDTO);

            YandexKassaReceiptCustomerDTO customerDTO = new YandexKassaReceiptCustomerDTO();
            customerDTO.setFull_name(request.getParams().get("customer_full_name"));
            customerDTO.setEmail(request.getParams().get("customer_email"));
            receiptDTO.setCustomer(customerDTO);
        }

        try {
            YandexKassaPaymentDTO result = createPayment(paymentDTO, params.get(PARAMETER_CLIENT_ID), params.get(PARAMETER_SECRET_KEY));

            Map<String, String> responseParams = new HashMap<>();
            responseParams.put("confirmation_url", result.getConfirmation().getConfirmation_url());
            return new PaymentExecutionResultDTO(false, result.getId(), responseParams);
        } catch (Exception e) {
            logger.error("Failed to execute payment with Yandex.Kassa", e);
            throw new PaymentExecutionException(e.getMessage());
        }

    }

    @Override
    public void processCallback(PaymentSystem paymentSystem, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) throws PaymentExecutionException {
        logger.info("Yandex.Kassa callback accepted");
        PropertyService propertyService;
        PaymentService paymentService;
        try {
            propertyService = context.getBean(PropertyService.class);
            paymentService = context.getBean(PaymentService.class);
        } catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        try {
            String requestBody = IOUtils.toString(request.getInputStream());
            logger.info("raw request: {}", requestBody);
            YandexKassaNotificationDTO notification = mapper.readValue(requestBody, YandexKassaNotificationDTO.class);
            logger.info(notification.toString());
            if (StringUtils.equalsIgnoreCase(notification.getEvent(), "payment.succeeded") && !StringUtils.isEmpty(notification.getObject().getId())) {
                YandexKassaPaymentDTO payment = getPayment(notification.getObject().getId(), propertyService.getPropertyValue("payment:yandex_kassa_shop_id"), propertyService.getPropertyValue("payment:yandex_kassa_secret"));
                if (StringUtils.equalsIgnoreCase(payment.getStatus(), "succeeded")) {
                    ProcessPaymentRequest req = new ProcessPaymentRequest();
                    req.setExternalIdentifier(payment.getId());
                    req.setRegisteredAmount(Double.parseDouble(payment.getAmount().getValue()));
                    req.setPaymentUID(payment.getId());
                    req.setCurrency(payment.getAmount().getCurrency());
                    paymentService.processPayment(paymentSystem.getId(), req, null);
                }
                else {
                    logger.warn("Payment not succeed, but event accepted: {}", notification.getObject().getId());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process Yandex.Kassa callback", e);
            throw new PaymentExecutionException(e.getMessage());
        }
    }

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return false;
    }

    private static YandexKassaPaymentDTO createPayment(YandexKassaCreatePaymentDTO request, String shopId, String secret) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();
        String uri = API_URL + "/v3/payments";
        logger.debug("request url: {}", uri);
        HttpPost sendMethod = new HttpPost(uri);
        sendMethod.addHeader("Authorization", "Basic " + Utils.getBase64Authorization(shopId, secret));
        sendMethod.addHeader("Content-Type", "application/json");
        sendMethod.addHeader("Accept", "application/json");
        sendMethod.addHeader("Idempotence-Key", UUID.randomUUID().toString());
        String requestBody = mapper.writeValueAsString(request);
        logger.debug("request body: {}", requestBody);
        sendMethod.setEntity(new StringEntity(requestBody, APPLICATION_JSON));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: {}", result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: {}", response);
        return mapper.readValue(response, YandexKassaPaymentDTO.class);
    }

    private static YandexKassaPaymentDTO getPayment(String id, String shopId, String secret) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();
        String uri = API_URL + "/v3/payments/" + id;
        logger.debug("request url: {}", uri);
        HttpGet sendMethod = new HttpGet(uri);
        sendMethod.addHeader("Authorization", "Basic " + Utils.getBase64Authorization(shopId, secret));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: {}", result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: {}", response);
        return mapper.readValue(response, YandexKassaPaymentDTO.class);
    }
}