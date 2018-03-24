package com.gracelogic.platform.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.Utils;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.dto.paypal.*;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayPalPaymentExecutor implements PaymentExecutor {
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_EXECUTE = "execute";
    private static final String ACTION = "action";

    private static final String SANDBOX_API_URL = "https://api.sandbox.paypal.com";
    private static final String PRODUCTION_API_URL = "https://api.paypal.com";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger(PayPalPaymentExecutor.class);

    @Override
    public PaymentExecutionResultDTO execute(String uniquePaymentIdentifier, UUID paymentSystemId, Long amount, String currencyCode, Long periodicity, ApplicationContext context, Map<String, String> params) throws PaymentExecutionException {
        if (params == null || !params.containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
        }

        PropertyService propertyService = null;
        PaymentService paymentService = null;
        try {
            propertyService = context.getBean(PropertyService.class);
            paymentService = context.getBean(PaymentService.class);
        } catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }

        String action = params.get(ACTION);
        String apiUrl = propertyService.getPropertyValueAsBoolean("payment:paypal_is_production") ? PRODUCTION_API_URL : SANDBOX_API_URL;
        PayPalOAuthResponseDTO accessToken = null;
        try {
            accessToken = token(apiUrl, propertyService.getPropertyValue("payment:paypal_client_id"), propertyService.getPropertyValue("payment:paypal_secret"));
            if (accessToken == null || StringUtils.isEmpty(accessToken.getAccess_token())) {
                throw new PaymentExecutionException("Access token is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaymentExecutionException("Failed to get access token");
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

            PayPalAmountDTO amountDTO = new PayPalAmountDTO(FinanceUtils.toFractional(amount), currencyCode);
            PayPalTransactionDTO transactionDTO = new PayPalTransactionDTO();
            transactionDTO.setAmount(amountDTO);
            createRequestDTO.getTransactions().add(transactionDTO);

            try {
                PayPalCreateResponseDTO responseDTO = create(apiUrl, accessToken.getAccess_token(), createRequestDTO);
                if (responseDTO.getId() == null) {
                    throw new PaymentExecutionException("PaymentId is null!");
                }
                Map<String, String> responseParams = new HashMap<>();
                responseParams.put("paymentId", responseDTO.getId());
                return new PaymentExecutionResultDTO(false, uniquePaymentIdentifier, responseParams);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }

        } else if (StringUtils.equalsIgnoreCase(action, ACTION_EXECUTE)) {
            PayPalExecuteRequestDTO executeRequestDTO = new PayPalExecuteRequestDTO();
            executeRequestDTO.setPayer_id(params.get("payerId"));

            try {
                PayPalExecuteResponseDTO responseDTO = execute(apiUrl, accessToken.getAccess_token(), params.get("paymentId"), executeRequestDTO);
                if (params.get("paymentId") != null && StringUtils.equalsIgnoreCase(responseDTO.getState(), "approved")) {
                    ProcessPaymentRequest req = new ProcessPaymentRequest();
                    req.setExternalIdentifier(uniquePaymentIdentifier);
                    req.setRegisteredAmount(FinanceUtils.toFractional(amount));
                    req.setPaymentUID(responseDTO.getId());
                    req.setCurrency(currencyCode);
                    try {
                        paymentService.processPayment(paymentSystemId, req, null);
                    } catch (Exception e) {
                        throw new PaymentExecutionException(e.getMessage());
                    }
                    return new PaymentExecutionResultDTO(true, uniquePaymentIdentifier, null);
                } else {
                    throw new PaymentExecutionException("Payment failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }
        } else {
            throw new PaymentExecutionException("Invalid action value");
        }
    }

    @Override
    public void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException("Not implemented");
    }

    private static PayPalOAuthResponseDTO token(String apiUrl, String clientId, String secret) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/oauth2/token";
        logger.debug("request url: " + uri);
        HttpPost sendMethod = new HttpPost(uri);
        sendMethod.addHeader("Authorization", "Basic " + Utils.getBase64Authorization(clientId, secret));
        sendMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
        sendMethod.addHeader("Accept", "application/json");
        String requestBody = "grant_type=client_credentials";
        logger.debug("request body: " + requestBody);
        sendMethod.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: " + result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: " + response);
        return mapper.readValue(response, PayPalOAuthResponseDTO.class);
    }

    private static PayPalCreateResponseDTO create(String apiUrl, String accessToken, PayPalCreateRequestDTO requestDTO) throws Exception {
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

    private static PayPalExecuteResponseDTO execute(String apiUrl, String accessToken, String id, PayPalExecuteRequestDTO requestDTO) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/payment/" + id + "/execute";
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