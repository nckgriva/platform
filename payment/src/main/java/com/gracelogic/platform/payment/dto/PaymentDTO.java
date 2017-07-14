package com.gracelogic.platform.payment.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.user.dto.UserDTO;

import java.util.UUID;


public class PaymentDTO extends IdObjectDTO {
    private UUID userId;
    private String userName;
    private UUID accountId;
    private String accountExternalIdentifier;
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

    public String getAccountExternalIdentifier() {
        return accountExternalIdentifier;
    }

    public void setAccountExternalIdentifier(String accountExternalIdentifier) {
        this.accountExternalIdentifier = accountExternalIdentifier;
    }

    public static PaymentDTO prepare(Payment model) {
        PaymentDTO dto = new PaymentDTO();
        IdObjectDTO.prepare(dto, model);

        if (model.getPaymentSystem() != null) {
            dto.setPaymentSystemId(model.getPaymentSystem().getId());
        }
        if (model.getPaymentState() != null) {
            dto.setPaymentStateId(model.getPaymentState().getId());
        }
        if (model.getAccount() != null) {
            dto.setAccountId(model.getAccount().getId());
        }

        dto.setRegisteredAmount(FinanceUtils.toFractional(model.getRegisteredAmount()));
        dto.setAmount(FinanceUtils.toFractional(model.getAmount()));
        dto.setTotalAmount(FinanceUtils.toFractional(model.getTotalAmount()));
        dto.setFee(FinanceUtils.toFractional(model.getFee()));
        dto.setDescription(model.getDescription());
        dto.setPaymentUID(model.getPaymentUID());

        return dto;
    }

    public static void enrich(PaymentDTO dto, Payment model) {
        if (model.getPaymentSystem() != null) {
            dto.setPaymentSystemName(model.getPaymentSystem().getName());
        }
        if (model.getPaymentState() != null) {
            dto.setPaymentStateName(model.getPaymentState().getName());
        }
        if (model.getAccount() != null) {
            dto.setAccountExternalIdentifier(model.getAccount().getExternalIdentifier());
            if (model.getAccount().getUser() != null) {
                dto.setUserId(model.getAccount().getUser().getId());
                dto.setUserName(UserDTO.formatUserName(model.getAccount().getUser()));
            }
        }
    }
}
