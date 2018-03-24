package com.gracelogic.platform.payment.dto.paypal;

public class PayPalOverrideChargeModelDTO {
    private String charge_id;
    private PayPalCurrencyDTO amount;

    public String getCharge_id() {
        return charge_id;
    }

    public void setCharge_id(String charge_id) {
        this.charge_id = charge_id;
    }

    public PayPalCurrencyDTO getAmount() {
        return amount;
    }

    public void setAmount(PayPalCurrencyDTO amount) {
        this.amount = amount;
    }
}
