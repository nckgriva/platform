package com.gracelogic.platform.market.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecuteOrderRequestDTO {
    private UUID orderId;
    private UUID paymentSystemId;
    private Map<String, String> params = new HashMap<>();

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getPaymentSystemId() {
        return paymentSystemId;
    }

    public void setPaymentSystemId(UUID paymentSystemId) {
        this.paymentSystemId = paymentSystemId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
