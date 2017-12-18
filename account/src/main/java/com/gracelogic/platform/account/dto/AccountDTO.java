package com.gracelogic.platform.account.dto;

import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.user.dto.UserDTO;

import java.util.UUID;

public class AccountDTO extends IdObjectDTO {
    private Long balance;
    private UUID accountTypeId;
    private String accountTypeName;
    private UUID currencyId;
    private String currencyName;
    private UUID userId;
    private String userName;
    private String externalIdentifier;

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Double getBalanceAsFractional() {
        if (this.balance != null) {
            return FinanceUtils.toFractional2Rounded(this.balance);
        }
        else {
            return null;
        }
    }

    public void setBalanceAsFractional(String sValue) {
        this.balance = FinanceUtils.stringToLong(sValue);
    }

    public UUID getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(UUID accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public UUID getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public static AccountDTO prepare(Account model) {
        AccountDTO dto = new AccountDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setBalance(model.getBalance());
        if (model.getAccountType() != null) {
            dto.setAccountTypeId(model.getAccountType().getId());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyId(model.getCurrency().getId());
        }
        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId()); 
        }
        dto.setExternalIdentifier(model.getExternalIdentifier());

        return dto;
    }

    public static void enrich(AccountDTO dto, Account model) {
        if (model.getUser() != null) {
            dto.setUserName(UserDTO.formatUserName(model.getUser()));
        }
        if (model.getAccountType() != null) {
            dto.setAccountTypeName(model.getAccountType().getName());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyName(model.getCurrency().getName());
        }
    }
}
