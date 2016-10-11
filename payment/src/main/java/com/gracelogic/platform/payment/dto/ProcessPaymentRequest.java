package com.gracelogic.platform.payment.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

/**
 * Author: Igor Parkhomenko
 * Date: 05.03.2015
 * Time: 14:52
 */
public class ProcessPaymentRequest extends PlatformRequest {
    private String accountNumber;
    private String paymentUID;
    private Double amount;
    private Double registeredAmount;
    private Double totalAmount;
    private Double fee;
    private String description;
    private String externalTypeUID;
    private String currency;

    public String getPaymentUID() {
        return paymentUID;
    }

    public void setPaymentUID(String paymentUID) {
        this.paymentUID = paymentUID;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalTypeUID() {
        return externalTypeUID;
    }

    public void setExternalTypeUID(String externalTypeUID) {
        this.externalTypeUID = externalTypeUID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRegisteredAmount() {
        return registeredAmount;
    }

    public void setRegisteredAmount(Double registeredAmount) {
        this.registeredAmount = registeredAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public ProcessPaymentRequest() {
    }

    public ProcessPaymentRequest(String accountNumber, Double registeredAmount) {
        this.accountNumber = accountNumber;
        this.registeredAmount = registeredAmount;
    }
}
