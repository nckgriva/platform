package com.gracelogic.platform.account.dto;

import com.gracelogic.platform.account.model.Transaction;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.user.dto.UserDTO;

import java.util.UUID;


public class TransactionDTO extends IdObjectDTO {
    private UUID userId;
    private String userName;
    private UUID accountId;
    private String accountExternalIdentifier;
    private UUID transactionTypeId;
    private String transactionTypeName;
    private Double amount;
    private Double balanceBefore;
    private Double balanceAfter;
    private UUID referenceObjectId;

    public String getAccountExternalIdentifier() {
        return accountExternalIdentifier;
    }

    public void setAccountExternalIdentifier(String accountExternalIdentifier) {
        this.accountExternalIdentifier = accountExternalIdentifier;
    }

    public UUID getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(UUID transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public Double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

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


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static TransactionDTO prepare(Transaction model) {
        TransactionDTO dto = new TransactionDTO();
        IdObjectDTO.prepare(dto, model);

        if (model.getAccount() != null) {
            dto.setAccountId(model.getAccount().getId());
        }
        if (model.getTransactionType() != null) {
            dto.setTransactionTypeId(model.getTransactionType().getId());
        }

        dto.setAmount(FinanceUtils.toFractional(model.getAmount()));
        dto.setBalanceBefore(FinanceUtils.toFractional(model.getBalanceBefore()));
        dto.setBalanceAfter(FinanceUtils.toFractional(model.getBalanceAfter()));

        return dto;
    }

    public static void enrich(TransactionDTO dto, Transaction model) {
        if (model.getAccount() != null) {
            dto.setAccountExternalIdentifier(model.getAccount().getExternalIdentifier());

            if (model.getAccount().getUser() != null) {
                dto.setUserId(model.getAccount().getUser().getId());
                dto.setUserName(UserDTO.formatUserName(model.getAccount().getUser()));
            }
        }
        if (model.getTransactionType() != null) {
            dto.setTransactionTypeName(model.getTransactionType().getName());
        }
    }

}
