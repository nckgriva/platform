package com.gracelogic.platform.payment.dto;


import java.util.Map;

public class PaymentExecutionResultDTO {
    private Boolean processed;
    private String externalIdentifier;
    private Map<String, String> params;

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public PaymentExecutionResultDTO(Boolean processed) {
        this.processed = processed;
    }

    public PaymentExecutionResultDTO(Boolean processed, Map<String, String> params) {
        this.processed = processed;
        this.params = params;
    }

    public PaymentExecutionResultDTO(Boolean processed, String externalIdentifier, Map<String, String> params) {
        this.processed = processed;
        this.externalIdentifier = externalIdentifier;
        this.params = params;
    }
}