package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalCreateRequestDTO {
    private String intent;
    private String experience_profile_id;
    private PayPalRedirectUrlsDTO redirect_urls;
    private PayPalPayerDTO payer;
    private List<PayPalTransactionDTO> transactions = new LinkedList<>();

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getExperience_profile_id() {
        return experience_profile_id;
    }

    public void setExperience_profile_id(String experience_profile_id) {
        this.experience_profile_id = experience_profile_id;
    }

    public PayPalRedirectUrlsDTO getRedirect_urls() {
        return redirect_urls;
    }

    public void setRedirect_urls(PayPalRedirectUrlsDTO redirect_urls) {
        this.redirect_urls = redirect_urls;
    }

    public PayPalPayerDTO getPayer() {
        return payer;
    }

    public void setPayer(PayPalPayerDTO payer) {
        this.payer = payer;
    }

    public List<PayPalTransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<PayPalTransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
