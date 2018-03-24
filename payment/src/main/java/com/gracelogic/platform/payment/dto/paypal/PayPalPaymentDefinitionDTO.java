package com.gracelogic.platform.payment.dto.paypal;

import java.util.LinkedList;
import java.util.List;

public class PayPalPaymentDefinitionDTO {
    private String id;
    private String name;
    private String type;
    private String frequency_interval;
    private String frequency;
    private String cycles;
    private PayPalCurrencyDTO amount;
    private List<PayPalChargeModelDTO> charge_models = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency_interval() {
        return frequency_interval;
    }

    public void setFrequency_interval(String frequency_interval) {
        this.frequency_interval = frequency_interval;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getCycles() {
        return cycles;
    }

    public void setCycles(String cycles) {
        this.cycles = cycles;
    }

    public PayPalCurrencyDTO getAmount() {
        return amount;
    }

    public void setAmount(PayPalCurrencyDTO amount) {
        this.amount = amount;
    }

    public List<PayPalChargeModelDTO> getCharge_models() {
        return charge_models;
    }

    public void setCharge_models(List<PayPalChargeModelDTO> charge_models) {
        this.charge_models = charge_models;
    }
}
