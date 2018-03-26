package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class PayPalPayerDTO {
    private String payment_method;
    private PayPalPayerInfoDTO payer_info;

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public PayPalPayerInfoDTO getPayer_info() {
        return payer_info;
    }

    public void setPayer_info(PayPalPayerInfoDTO payer_info) {
        this.payer_info = payer_info;
    }
}
