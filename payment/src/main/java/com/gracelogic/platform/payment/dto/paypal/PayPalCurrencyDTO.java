package com.gracelogic.platform.payment.dto.paypal;

public class PayPalCurrencyDTO {
    private String currency;
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrency() {

        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PayPalCurrencyDTO(String currency, Double value) {
        this.currency = currency;
        this.value = value;
    }
}
