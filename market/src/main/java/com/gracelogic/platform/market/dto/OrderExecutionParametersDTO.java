package com.gracelogic.platform.market.dto;


public class OrderExecutionParametersDTO {
    private Boolean processed;
    private String redirectUrl;

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public OrderExecutionParametersDTO(Boolean processed, String redirectUrl) {
        this.processed = processed;
        this.redirectUrl = redirectUrl;
    }
}
