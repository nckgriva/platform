package com.gracelogic.platform.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.Utils;
import com.gracelogic.platform.payment.dto.PaymentExecutionRequestDTO;
import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.dto.paypal.*;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.web.dto.EmptyResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayPalPaymentExecutor implements PaymentExecutor {
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_EXECUTE = "execute";
    private static final String ACTION = "action";

    private static final String SANDBOX_API_URL = "https://api.sandbox.paypal.com";
    private static final String PRODUCTION_API_URL = "https://api.paypal.com";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger(PayPalPaymentExecutor.class);

    private final static String BILLING_AGREEMENT_ID_REGEXP = "billing-agreements\\/(.*?)\\/agreement-execute";


    @Override
    public PaymentExecutionResultDTO execute(PaymentExecutionRequestDTO request, ApplicationContext context) throws PaymentExecutionException {
        //Initilaize
        PropertyService propertyService = null;
        PaymentService paymentService = null;
        try {
            propertyService = context.getBean(PropertyService.class);
            paymentService = context.getBean(PaymentService.class);
        } catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage());
        }
        String apiUrl = propertyService.getPropertyValueAsBoolean("payment:paypal_is_production") ? PRODUCTION_API_URL : SANDBOX_API_URL;

        //Get access token
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

        if (request.getPeriodicity() == null) {
            return executePayment(request, accessToken, apiUrl, propertyService, paymentService);
        } else {
            return executeBillingAgreement(request, accessToken, apiUrl, propertyService, paymentService);
        }
    }

    @Override
    public void processCallback(UUID paymentSystemId, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
        logger.info("PayPal callback:");
        logger.info(request.getQueryString());
    }

    @Override
    public boolean isRecurringPaymentsAllowed() {
        return true;
    }

    private PaymentExecutionResultDTO executePayment(PaymentExecutionRequestDTO request, PayPalOAuthResponseDTO accessToken, String apiUrl, PropertyService propertyService, PaymentService paymentService) throws PaymentExecutionException {
        String action = request.getParams().get(ACTION);
        if (request.getParams() == null || !request.getParams().containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
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

            PayPalAmountDTO amountDTO = new PayPalAmountDTO(FinanceUtils.toFractional(request.getAmount()), request.getCurrencyCode());
            PayPalTransactionDTO transactionDTO = new PayPalTransactionDTO();
            transactionDTO.setAmount(amountDTO);
            createRequestDTO.getTransactions().add(transactionDTO);

            try {
                PayPalCreateResponseDTO responseDTO = createPayment(apiUrl, accessToken.getAccess_token(), createRequestDTO);
                if (responseDTO.getId() == null) {
                    throw new PaymentExecutionException("PaymentId is null!");
                }
                Map<String, String> responseParams = new HashMap<>();
                responseParams.put("paymentId", responseDTO.getId());
                return new PaymentExecutionResultDTO(false, request.getUniquePaymentIdentifier(), responseParams);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }

        } else if (StringUtils.equalsIgnoreCase(action, ACTION_EXECUTE)) {
            PayPalExecuteRequestDTO executeRequestDTO = new PayPalExecuteRequestDTO();
            executeRequestDTO.setPayer_id(request.getParams().get("payerId"));

            try {
                PayPalExecuteResponseDTO responseDTO = executePayment(apiUrl, accessToken.getAccess_token(), request.getParams().get("paymentId"), executeRequestDTO);
                if (request.getParams().get("paymentId") != null && StringUtils.equalsIgnoreCase(responseDTO.getState(), "approved")) {
                    ProcessPaymentRequest req = new ProcessPaymentRequest();
                    req.setExternalIdentifier(request.getUniquePaymentIdentifier());
                    req.setRegisteredAmount(FinanceUtils.toFractional(request.getAmount()));
                    req.setPaymentUID(responseDTO.getId());
                    req.setCurrency(request.getCurrencyCode());
                    try {
                        paymentService.processPayment(request.getPaymentSystemId(), req, null);
                    } catch (Exception e) {
                        throw new PaymentExecutionException(e.getMessage());
                    }
                    return new PaymentExecutionResultDTO(true, request.getUniquePaymentIdentifier(), null);
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


    private PaymentExecutionResultDTO executeBillingAgreement(PaymentExecutionRequestDTO request, PayPalOAuthResponseDTO accessToken, String apiUrl, PropertyService propertyService, PaymentService paymentService) throws PaymentExecutionException {
        if (request.getParams() == null || !request.getParams().containsKey(ACTION)) {
            throw new PaymentExecutionException("Not specified action");
        }

        String action = request.getParams().get(ACTION);
        if (StringUtils.equalsIgnoreCase(action, ACTION_CREATE)) {

            //Create & activate new plan
            PayPalPlanDTO planDTO = new PayPalPlanDTO();
            planDTO.setName(request.getName());
            planDTO.setDescription(request.getDescription());
            planDTO.setType(request.getRecurringCycles() == null ? "INFINITE" : "FIXED");

            PayPalPaymentDefinitionDTO definitionDTO = new PayPalPaymentDefinitionDTO();
            definitionDTO.setAmount(new PayPalCurrencyDTO(request.getCurrencyCode(), FinanceUtils.toFractional(request.getAmount())));
            definitionDTO.setCycles(request.getRecurringCycles());
            definitionDTO.setName("Regular payment definition");
            definitionDTO.setType("REGULAR");
            definitionDTO.setFrequency("DAY");
            definitionDTO.setFrequency_interval(request.getPeriodicity() / 1000 / 60 / 60 / 24);
            planDTO.getPayment_definitions().add(definitionDTO);

            PayPalMerchantPreferencesDTO merchantPreferencesDTO = new PayPalMerchantPreferencesDTO();
            merchantPreferencesDTO.setAuto_bill_amount("YES");
            merchantPreferencesDTO.setInitial_fail_amount_action("CONTINUE");
            merchantPreferencesDTO.setReturn_url(propertyService.getPropertyValue("payment:paypal_return_url"));
            merchantPreferencesDTO.setCancel_url(propertyService.getPropertyValue("payment:paypal_cancel_url"));
            merchantPreferencesDTO.setMax_fail_attempts(0);
            planDTO.setMerchant_preferences(merchantPreferencesDTO);
            try {
                planDTO = createPlan(apiUrl, accessToken.getAccess_token(), planDTO);
                if (planDTO.getId() == null) {
                    throw new PaymentExecutionException("PlanId is null!");
                }
                activatePlan(apiUrl, accessToken.getAccess_token(), planDTO.getId());

            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }

            //Create billing agreement
            PayPalBillingAgreementDTO billingAgreementDTO = new PayPalBillingAgreementDTO();
            billingAgreementDTO.setName(request.getName());
            billingAgreementDTO.setDescription(request.getDescription());

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            gc.add(Calendar.DATE, 1);
            gc.add(Calendar.MINUTE, 1);
            billingAgreementDTO.setStart_date(gc.getTime());
            PayPalPayerDTO payerDTO = new PayPalPayerDTO();
            payerDTO.setPayment_method("paypal");
            billingAgreementDTO.setPayer(payerDTO);

            PayPalPlanDTO planDTO1 = new PayPalPlanDTO();
            planDTO1.setId(planDTO.getId());
            billingAgreementDTO.setPlan(planDTO1);

            try {
                billingAgreementDTO = createBillingAgreement(apiUrl, accessToken.getAccess_token(), billingAgreementDTO);
                for (PayPalLinkDescriptionDTO l : billingAgreementDTO.getLinks()) {
                    if (StringUtils.equalsIgnoreCase(l.getRel(), "execute")) {
                        billingAgreementDTO.setId(extractAgreementId(l.getHref()));
                        break;
                    }
                }
                if (billingAgreementDTO.getId() == null) {
                    throw new PaymentExecutionException("BillingAgreement is null!");
                }
                Map<String, String> responseParams = new HashMap<>();
                responseParams.put("paymentId", billingAgreementDTO.getId());
                return new PaymentExecutionResultDTO(false, request.getUniquePaymentIdentifier(), responseParams);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }
        } else if (StringUtils.equalsIgnoreCase(action, ACTION_EXECUTE)) {
            try {
                PayPalBillingAgreementDTO billingAgreementDTO = executeBillingAgreement(apiUrl, accessToken.getAccess_token(), request.getParams().get("paymentId"));
                if (billingAgreementDTO.getId() == null) {
                    throw new PaymentExecutionException("BillingAgreement is null!");
                }
                return new PaymentExecutionResultDTO(true, request.getUniquePaymentIdentifier(), request.getParams());
            } catch (Exception e) {
                e.printStackTrace();
                throw new PaymentExecutionException(e.getMessage());
            }
        } else {
            throw new PaymentExecutionException("Invalid action value");
        }
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

    private static PayPalCreateResponseDTO createPayment(String apiUrl, String accessToken, PayPalCreateRequestDTO requestDTO) throws Exception {
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

    private static PayPalExecuteResponseDTO executePayment(String apiUrl, String accessToken, String id, PayPalExecuteRequestDTO requestDTO) throws Exception {
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

    private static PayPalPlanDTO createPlan(String apiUrl, String accessToken, PayPalPlanDTO requestDTO) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/billing-plans";
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
        return mapper.readValue(response, PayPalPlanDTO.class);
    }

    private static void activatePlan(String apiUrl, String accessToken, String planId) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/billing-plans/" + planId;
        logger.debug("request url: " + uri);
        HttpPatch sendMethod = new HttpPatch(uri);
        sendMethod.addHeader("Authorization", "Bearer " + accessToken);
        sendMethod.addHeader("Content-Type", "application/json");
        PayPalPathDTO payPalPathDTO = new PayPalPathDTO();
        payPalPathDTO.setOp("replace");
        payPalPathDTO.setPath("/");
        payPalPathDTO.getValue().put("state", "ACTIVE");
        List<PayPalPathDTO> list = new LinkedList<>();
        list.add(payPalPathDTO);
        String requestBody = mapper.writeValueAsString(list);
        logger.debug("request body: " + requestBody);
        sendMethod.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: " + result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: " + response);
    }

    private static PayPalBillingAgreementDTO createBillingAgreement(String apiUrl, String accessToken, PayPalBillingAgreementDTO requestDTO) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/billing-agreements";
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
        return mapper.readValue(response, PayPalBillingAgreementDTO.class);
    }

    private static PayPalBillingAgreementDTO executeBillingAgreement(String apiUrl, String accessToken, String billingAgreementId) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getMultithreadedUnsecuredClient();

        String uri = apiUrl + "/v1/payments/billing-agreements/" + billingAgreementId + "/agreement-execute";
        logger.debug("request url: " + uri);
        HttpPost sendMethod = new HttpPost(uri);
        sendMethod.addHeader("Authorization", "Bearer " + accessToken);
        sendMethod.addHeader("Content-Type", "application/json");
        String requestBody = mapper.writeValueAsString(EmptyResponse.getInstance());
        logger.debug("request body: " + requestBody);
        sendMethod.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse result = httpClient.execute(sendMethod);
        logger.debug("response status: " + result.getStatusLine().getStatusCode());
        HttpEntity entity = result.getEntity();
        String response = EntityUtils.toString(entity);
        logger.debug("response body: " + response);
        return mapper.readValue(response, PayPalBillingAgreementDTO.class);
    }

    private static String extractAgreementId(String url) {
        Pattern pattern = Pattern.compile(BILLING_AGREEMENT_ID_REGEXP);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}