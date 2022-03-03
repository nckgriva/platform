package com.gracelogic.platform.payment.dto;

import java.util.Map;
import java.util.UUID;

public class PaymentExecutionRequestDTO {
    private UUID authorizedUserId;
    private String uniquePaymentIdentifier;
    private UUID paymentSystemId;
    private Long amount;
    private String currencyCode;
    private Long periodicity; //periodicity between recurring payments
    private String name; //payment name for gateway
    private String description; //payment description for gateway
    private Integer recurringCycles; //If null - infinity
    private Map<String, String> params;

    public String getUniquePaymentIdentifier() {
        return uniquePaymentIdentifier;
    }

    public void setUniquePaymentIdentifier(String uniquePaymentIdentifier) {
        this.uniquePaymentIdentifier = uniquePaymentIdentifier;
    }

    public UUID getPaymentSystemId() {
        return paymentSystemId;
    }

    public void setPaymentSystemId(UUID paymentSystemId) {
        this.paymentSystemId = paymentSystemId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Long periodicity) {
        this.periodicity = periodicity;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRecurringCycles() {
        return recurringCycles;
    }

    public void setRecurringCycles(Integer recurringCycles) {
        this.recurringCycles = recurringCycles;
    }

    public UUID getAuthorizedUserId() {
        return authorizedUserId;
    }

    public void setAuthorizedUserId(UUID authorizedUserId) {
        this.authorizedUserId = authorizedUserId;
    }
}
