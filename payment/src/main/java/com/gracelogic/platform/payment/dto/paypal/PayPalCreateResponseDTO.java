package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalCreateResponseDTO {
    private String id;
    private String intent;
    private String state;
    private PayPalPayerDTO payer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PayPalPayerDTO getPayer() {
        return payer;
    }

    public void setPayer(PayPalPayerDTO payer) {
        this.payer = payer;
    }
}
