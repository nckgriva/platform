package com.gracelogic.platform.market.model;

import com.gracelogic.platform.account.model.Currency;
import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.model.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "ORDER")
public class Order extends IdObject<UUID> {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_STATE_ID", nullable = false)
    private OrderState orderState;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DISCOUNT_ID", nullable = true)
    private Discount discount;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PAYMENT_SYSTEM_ID", nullable = true)
    private PaymentSystem paymentSystem; //В состоянии DRAFT способ оплаты может быть не выбран

    @Column(name = "AMOUNT", nullable = false)
    private Long amount; //Сумма всех продуктов внутри заказа

    @Column(name = "DISCOUNT_AMOUNT", nullable = false)
    private Long discountAmount; //Вычисленный размер скидки

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Long totalAmount; //Общая сумма к оплате с учётом скидки

    @Column(name = "PAID", nullable = false)
    private Long paid; //Оплачено пользователем

    @Column(name = "EXTERNAL_IDENTIFIER", nullable = true)
    private String externalIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //Валюта, в которой необходимо провести оплату
    @JoinColumn(name = "TARGET_CURRENCY_ID", nullable = false)
    private Currency targetCurrency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OWNERSHIP_TYPE_ID", nullable = false)
    private OwnershipType ownershipType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //Родительский заказ, используется для подписок
    @JoinColumn(name = "PARENT_ORDER_ID", nullable = true)
    private Order parentOrder;

    @Column(name = "PERIODICITY", nullable = true)
    private Long periodicity; //Периодичность для подписки

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getPaid() {
        return paid;
    }

    public void setPaid(Long paid) {
        this.paid = paid;
    }

    public PaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(PaymentSystem paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Order getParentOrder() {
        return parentOrder;
    }

    public void setParentOrder(Order parentOrder) {
        this.parentOrder = parentOrder;
    }

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }

    public Long getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Long periodicity) {
        this.periodicity = periodicity;
    }
}
