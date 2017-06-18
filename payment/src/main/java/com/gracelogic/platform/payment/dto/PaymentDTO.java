package com.gracelogic.platform.payment.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.model.Payment;

import java.util.UUID;


public class PaymentDTO extends IdObjectDTO {
    private UUID userId;
    private String userName;
    private UUID accountId;
    private UUID paymentSystemId;
    private String paymentSystemName;
    private UUID paymentStateId;
    private String paymentStateName;
    private String paymentUID;
    private Double registeredAmount;
    private Double amount;
    private Double fee;
    private Double totalAmount;
    private String description;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getPaymentSystemId() {
        return paymentSystemId;
    }

    public void setPaymentSystemId(UUID paymentSystemId) {
        this.paymentSystemId = paymentSystemId;
    }

    public UUID getPaymentStateId() {
        return paymentStateId;
    }

    public void setPaymentStateId(UUID paymentStateId) {
        this.paymentStateId = paymentStateId;
    }

    public String getPaymentUID() {
        return paymentUID;
    }

    public void setPaymentUID(String paymentUID) {
        this.paymentUID = paymentUID;
    }

    public Double getRegisteredAmount() {
        return registeredAmount;
    }

    public void setRegisteredAmount(Double registeredAmount) {
        this.registeredAmount = registeredAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentSystemName() {
        return paymentSystemName;
    }

    public void setPaymentSystemName(String paymentSystemName) {
        this.paymentSystemName = paymentSystemName;
    }

    public String getPaymentStateName() {
        return paymentStateName;
    }

    public void setPaymentStateName(String paymentStateName) {
        this.paymentStateName = paymentStateName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static PaymentDTO prepare(Payment payment) {
        PaymentDTO model = new PaymentDTO();
        IdObjectDTO.prepare(model, payment);

        if (payment.getUser() != null) {
            model.setUserId(payment.getUser().getId());
//            model.setUserName(ExtendedUserDTO.formatUserName(payment.getUser()));
        }
        if (payment.getPaymentSystem() != null) {
            model.setPaymentSystemId(payment.getPaymentSystem().getId());
            model.setPaymentSystemName(payment.getPaymentSystem().getName());
        }
        if (payment.getPaymentState() != null) {
            model.setPaymentStateId(payment.getPaymentState().getId());
            model.setPaymentStateName(payment.getPaymentState().getName());
        }
        if (payment.getAccount() != null) {
            model.setAccountId(payment.getAccount().getId());
        }

        model.setRegisteredAmount(FinanceUtils.toFractional(payment.getRegisteredAmount()));
        model.setAmount(FinanceUtils.toFractional(payment.getAmount()));
        model.setTotalAmount(FinanceUtils.toFractional(payment.getTotalAmount()));
        model.setFee(FinanceUtils.toFractional(payment.getFee()));
        model.setDescription(payment.getDescription());
        model.setPaymentUID(payment.getPaymentUID());

        return model;
    }
}
