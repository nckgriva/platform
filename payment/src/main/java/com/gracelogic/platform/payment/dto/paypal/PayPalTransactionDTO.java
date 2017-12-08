package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalTransactionDTO {
    private PayPalAmountDTO amount;

    public PayPalAmountDTO getAmount() {
        return amount;
    }

    public void setAmount(PayPalAmountDTO amount) {
        this.amount = amount;
    }
}
