package com.gracelogic.platform.market.dto;

import java.util.UUID;

public class ExecuteOrderRequestDTO {
    private UUID orderId;
    private UUID paymentSystemId;

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
}
