package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
public class PayPalPaymentDefinitionDTO {
    private String id;
    private String name;
    private String type;
    private Long frequency_interval;
    private String frequency;
    private Integer cycles;
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

    public Long getFrequency_interval() {
        return frequency_interval;
    }

    public void setFrequency_interval(Long frequency_interval) {
        this.frequency_interval = frequency_interval;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getCycles() {
        return cycles;
    }

    public void setCycles(Integer cycles) {
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
