package com.gracelogic.platform.payment.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.dictionary.model.Dictionary;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "PAYMENT_SYSTEM")
public class PaymentSystem extends IdObject<UUID> implements Dictionary {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = true, length = 4000)
    private String description;

    @Column(name = "ALLOWED_ADDRESSES", nullable = true)
    private String allowedAddresses;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;

    @Column(name = "FEE", nullable = false)
    private Long fee;

    @Column(name = "IS_FEE_INCLUDED", nullable = false)
    private Boolean feeIncluded;

    @Column(name = "PAYMENT_EXECUTOR_CLASS", nullable = true)
    private String paymentExecutorClass;

    @Type(JsonStringType.class)
    @Column(columnDefinition = "json", nullable = true)
    private String fields;

    @Column(name = "IS_RECURRING_AVAILABLE", nullable = false)
    private Boolean recurringAvailable;

    @Column(name = SORT_ORDER, nullable = true)
    private Integer sortOrder;


    @Override
    public UUID getId() {
        return id;
    }

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

    public String getName() {
        return name;
    }

    @Override
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getCode() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Boolean getFeeIncluded() {
        return feeIncluded;
    }

    public void setFeeIncluded(Boolean feeIncluded) {
        this.feeIncluded = feeIncluded;
    }

    public String getAllowedAddresses() {
        return allowedAddresses;
    }

    public void setAllowedAddresses(String allowedAddresses) {
        this.allowedAddresses = allowedAddresses;
    }

    public String getPaymentExecutorClass() {
        return paymentExecutorClass;
    }

    public void setPaymentExecutorClass(String paymentExecutorClass) {
        this.paymentExecutorClass = paymentExecutorClass;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Boolean getRecurringAvailable() {
        return recurringAvailable;
    }

    public void setRecurringAvailable(Boolean recurringAvailable) {
        this.recurringAvailable = recurringAvailable;
    }
}
