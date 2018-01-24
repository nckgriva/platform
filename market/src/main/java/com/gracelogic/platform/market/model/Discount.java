package com.gracelogic.platform.market.model;

import com.gracelogic.platform.account.model.Currency;
import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "DISCOUNT", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"SECRET_CODE"})})
public class Discount extends IdObject<UUID> {
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

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;

    @Column(name = "IS_REUSABLE", nullable = false)
    private Boolean reusable;

    @Column(name = "IS_ONCE_FOR_USER", nullable = false)
    private Boolean onceForUser;

    @Column(name = "IS_USED", nullable = false)
    private Boolean used; //Актуально только для случая с reusable = false

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "USED_FOR_ORDER_ID", nullable = true)
    private Order usedForOrder; //Актуально только для случая с reusable = false

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DISCOUNT_TYPE_ID", nullable = false)
    private DiscountType discountType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "CURRENCY_ID", nullable = true)
    private Currency currency;

    @Column(name = "SECRET_CODE", nullable = false)
    private String secretCode;

    @Column(name = "AMOUNT", nullable = true)
    private Long amount;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReusable() {
        return reusable;
    }

    public void setReusable(Boolean reusable) {
        this.reusable = reusable;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Order getUsedForOrder() {
        return usedForOrder;
    }

    public void setUsedForOrder(Order usedForOrder) {
        this.usedForOrder = usedForOrder;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Boolean getOnceForUser() {
        return onceForUser;
    }

    public void setOnceForUser(Boolean onceForUser) {
        this.onceForUser = onceForUser;
    }
}
