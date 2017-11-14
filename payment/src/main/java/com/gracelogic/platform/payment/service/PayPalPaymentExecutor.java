package com.gracelogic.platform.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.paypal.*;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


import java.util.Map;

public class PayPalPaymentExecutor implements PaymentExecutor {
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_EXECUTE = "execute";
    private static final String ACTION = "action";

    private String SANDBOX_API_URL = "https://api.sandbox.paypal.com";
    private String PRODUCTION_API_URL = "";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger(PayPalPaymentExecutor.class);

    @Override
    public PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, Long amount, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException {
        if (params == null || !params.containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
        }

        String action = params.get(ACTION);
        PropertyService propertyService = context.getBean("propertyService", PropertyService.class);
        String apiUrl = null;
        String accessToken = null;
        if (propertyService.getPropertyValueAsBoolean("payment:paypal_is_production")) {
            apiUrl = PRODUCTION_API_URL;
        } else {
            apiUrl = SANDBOX_API_URL;
        }

        if (StringUtils.equalsIgnoreCase(action, ACTION_CREATE)) {
            PayPalCreateRequestDTO createRequestDTO = new PayPalCreateRequestDTO();
            createRequestDTO.setIntent("sale");

            PayPalRedirectUrlsDTO payPalRedirectUrlsDTO = new PayPalRedirectUrlsDTO();
            payPalRedirectUrlsDTO.setReturn_url(propertyService.getPropertyValue("payment:paypal_return_url"));
            payPalRedirectUrlsDTO.setCancel_url(propertyService.getPropertyValue("payment:paypal_cancel_url"));
            createRequestDTO.setRedirect_urls(payPalRedirectUrlsDTO);

            PayPalPayerDTO payPalPayerDTO = new PayPalPayerDTO();
            payPalPayerDTO.setPayment_method("paypal");
            createRequestDTO.setPayer(payPalPayerDTO);

            try {
                PayPalCreateResponseDTO responseDTO = create(apiUrl, accessToken, createRequestDTO);
                return new PaymentExecutionResultDTO(false, responseDTO.getId(), null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }

        } else if (StringUtils.equalsIgnoreCase(action, ACTION_EXECUTE)) {
            PayPalExecuteRequestDTO executeRequestDTO = new PayPalExecuteRequestDTO();
            executeRequestDTO.setPayer_id(null);

            try {
                PayPalExecuteResponseDTO responseDTO = execute(apiUrl, accessToken, uniquePaymentIdentifier, executeRequestDTO);
                return new PaymentExecutionResultDTO(true, responseDTO.getId(), null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }
        } else {
            throw new PaymentExecutionException("Invalid action value");
        }
    }

    private PayPalCreateResponseDTO create(String apiUrl, String accessToken, PayPalCreateRequestDTO requestDTO) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/payment";
        logger.debug("request url: " + uri);
        HttpPost sendMethod = new HttpPost(uri);
        sendMethod.addHeader("Authorization", "Bearer " + accessToken);
        sendMethod.addHeader("Content-Type", "application/json");
        String requestBody = mapper.writeValueAsString(requestDTO);
        logger.debug("request body: " + requestBody);
        sendMethod.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: " + result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: " + response);
        return mapper.readValue(response, PayPalCreateResponseDTO.class);
    }

    private PayPalExecuteResponseDTO execute(String apiUrl, String accessToken, String id, PayPalExecuteRequestDTO requestDTO) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/payment/" + id + "/execute/";
        logger.debug("request url: " + uri);
        HttpPost sendMethod = new HttpPost(uri);
        sendMethod.addHeader("Authorization", "Bearer " + accessToken);
        sendMethod.addHeader("Content-Type", "application/json");
        String requestBody = mapper.writeValueAsString(requestDTO);
        logger.debug("request body: " + requestBody);
        sendMethod.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: " + result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: " + response);
        return mapper.readValue(response, PayPalExecuteResponseDTO.class);
    }
}