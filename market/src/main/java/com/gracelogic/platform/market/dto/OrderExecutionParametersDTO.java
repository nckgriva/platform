package com.gracelogic.platform.market.dto;


public class OrderExecutionParametersDTO {
    private String gatewayUrl;
    private Boolean needRedirect;

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public Boolean getNeedRedirect() {
        return needRedirect;
    }

    public void setNeedRedirect(Boolean needRedirect) {
        this.needRedirect = needRedirect;
    }

    public OrderExecutionParametersDTO(String gatewayUrl, Boolean needRedirect) {
        this.gatewayUrl = gatewayUrl;
        this.needRedirect = needRedirect;
    }
}
