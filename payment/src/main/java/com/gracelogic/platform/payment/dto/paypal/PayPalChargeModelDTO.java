package com.gracelogic.platform.payment.dto.paypal;

public class PayPalChargeModelDTO {
    private String id;
    private String type;
    private PayPalCurrencyDTO amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PayPalCurrencyDTO getAmount() {
        return amount;
    }

    public void setAmount(PayPalCurrencyDTO amount) {
        this.amount = amount;
    }
}
