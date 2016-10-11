package com.gracelogic.platform.onec.dto;

import java.util.Date;

/**
 * Платежное поручение
 */
public class BankPayment {
    private String number;

    private OrganizationAccount payerBankAccount = new OrganizationAccount();

    private OrganizationAccount recipientBankAccount = new OrganizationAccount();

    private Double amount;

    private String description;

    private Date createDate;

    private Date processedDate;

    private Date incomingDate;

    private Date outgoingDate;

    //Transient
    private boolean dateIsNull;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public OrganizationAccount getPayerBankAccount() {
        return payerBankAccount;
    }

    public void setPayerBankAccount(OrganizationAccount payerBankAccount) {
        this.payerBankAccount = payerBankAccount;
    }

    public OrganizationAccount getRecipientBankAccount() {
        return recipientBankAccount;
    }

    public void setRecipientBankAccount(OrganizationAccount recipientBankAccount) {
        this.recipientBankAccount = recipientBankAccount;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public Date getIncomingDate() {
        return incomingDate;
    }

    public void setIncomingDate(Date incomingDate) {
        this.incomingDate = incomingDate;
    }

    public Date getOutgoingDate() {
        return outgoingDate;
    }

    public void setOutgoingDate(Date outgoingDate) {
        this.outgoingDate = outgoingDate;
    }

    public boolean isDateIsNull() {
        return dateIsNull;
    }

    public void setDateIsNull(boolean dateIsNull) {
        this.dateIsNull = dateIsNull;
    }

    @Override
    public String toString() {
        return "BankPayment{" +
                "number='" + number + '\'' +
                ", payerBankAccount=" + payerBankAccount +
                ", recipientBankAccount=" + recipientBankAccount +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", createDate=" + createDate +
                ", processedDate=" + processedDate +
                ", incomingDate=" + incomingDate +
                ", outgoingDate=" + outgoingDate +
                '}';
    }
}
