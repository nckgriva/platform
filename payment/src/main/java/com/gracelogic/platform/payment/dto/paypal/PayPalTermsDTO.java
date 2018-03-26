package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class PayPalTermsDTO {
    private String id;
    private String type;
    private PayPalCurrencyDTO max_billing_amount;
    private String occurrences;
    private PayPalCurrencyDTO amount_range;
    private String buyer_editable;

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

    public PayPalCurrencyDTO getMax_billing_amount() {
        return max_billing_amount;
    }

    public void setMax_billing_amount(PayPalCurrencyDTO max_billing_amount) {
        this.max_billing_amount = max_billing_amount;
    }

    public String getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(String occurrences) {
        this.occurrences = occurrences;
    }

    public PayPalCurrencyDTO getAmount_range() {
        return amount_range;
    }

    public void setAmount_range(PayPalCurrencyDTO amount_range) {
        this.amount_range = amount_range;
    }

    public String getBuyer_editable() {
        return buyer_editable;
    }

    public void setBuyer_editable(String buyer_editable) {
        this.buyer_editable = buyer_editable;
    }
}
