package com.gracelogic.platform.account.model;


import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.12.14
 * Time: 12:34
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "ACCOUNT", schema = JPAProperties.DEFAULT_SCHEMA)
public class Account extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "BALANCE", nullable = false)
    private Long balance;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_TYPE_ID", nullable = false)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_CURRENCY_ID", nullable = true)
    private AccountCurrency accountCurrency;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "EXTERNAL_IDENTIFIER", nullable = true)
    private String externalIdentifier;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public AccountCurrency getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(AccountCurrency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }
}
