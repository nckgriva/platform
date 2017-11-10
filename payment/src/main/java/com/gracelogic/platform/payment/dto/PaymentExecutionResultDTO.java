package com.gracelogic.platform.payment.dto;


import java.util.Map;

public class PaymentExecutionResultDTO {
    private Boolean processed;
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

    public PaymentExecutionResultDTO(Boolean processed) {
        this.processed = processed;
    }

    public PaymentExecutionResultDTO(Boolean processed, Map<String, String> params) {
        this.processed = processed;
        this.params = params;
    }
}