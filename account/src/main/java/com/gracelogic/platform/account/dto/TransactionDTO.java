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

    public static TransactionDTO prepare(Transaction transaction) {
        TransactionDTO model = new TransactionDTO();
        IdObjectDTO.prepare(model, transaction);

        if (transaction.getAccount() != null) {
            model.setAccountId(transaction.getAccount().getId());
        }
        if (transaction.getTransactionType() != null) {
            model.setTransactionTypeId(transaction.getTransactionType().getId());
        }

        model.setAmount(FinanceUtils.toFractional(transaction.getAmount()));
        model.setBalanceBefore(FinanceUtils.toFractional(transaction.getBalanceBefore()));
        model.setBalanceAfter(FinanceUtils.toFractional(transaction.getBalanceAfter()));

        return model;
    }

    public static void enrich(TransactionDTO model, Transaction entity) {
        if (entity.getAccount() != null) {
            model.setAccountExternalIdentifier(entity.getAccount().getExternalIdentifier());

            if (entity.getAccount().getUser() != null) {
                model.setUserId(entity.getAccount().getUser().getId());
                model.setUserName(UserDTO.formatUserName(entity.getAccount().getUser()));
            }
        }
        if (entity.getTransactionType() != null) {
            model.setTransactionTypeName(entity.getTransactionType().getName());
        }
    }

}
