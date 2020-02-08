package com.gracelogic.platform.payment.model;


import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "PAYMENT")
public class Payment extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "com.gracelogic.platform.db.type.UUIDCustomType")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private Account account;

    @Column(name = "EXTERNAL_IDENTIFIER", nullable = true)
    private String externalIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PAYMENT_SYSTEM_ID", nullable = false)
    private PaymentSystem paymentSystem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PAYMENT_STATE_ID", nullable = false)
    private PaymentState paymentState;

    @Column(name = "PAYMENT_UID", nullable = true)
    private String paymentUID;

    @Column(name = "EXTERNAL_TYPE_UID", nullable = true)
    private String externalTypeUID;

    @Column(name = "REGISTERED_AMOUNT", nullable = false)
    private Long registeredAmount;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "DESCRIPTION", nullable = true, length = 4000)
    private String description;

    @Column(name = "FEE", nullable = false)
    private Long fee;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Long totalAmount;

    //TODO: Добавить currency?

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EXECUTED_BY_USER_ID", nullable = true)
    private User executedByUser;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public PaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(PaymentSystem paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public String getPaymentUID() {
        return paymentUID;
    }

    public void setPaymentUID(String paymentSystemUID) {
        this.paymentUID = paymentSystemUID;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentState getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getRegisteredAmount() {
        return registeredAmount;
    }

    public void setRegisteredAmount(Long registeredAmount) {
        this.registeredAmount = registeredAmount;
    }

    public String getExternalTypeUID() {
        return externalTypeUID;
    }

    public void setExternalTypeUID(String externalTypeUID) {
        this.externalTypeUID = externalTypeUID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getExecutedByUser() {
        return executedByUser;
    }

    public void setExecutedByUser(User executedByUser) {
        this.executedByUser = executedByUser;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }
}
