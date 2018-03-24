package com.gracelogic.platform.payment.dto.paypal;

public class PayPalCurrencyDTO {
    private String currency;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {

        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
