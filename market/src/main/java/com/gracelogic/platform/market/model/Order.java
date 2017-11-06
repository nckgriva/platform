package com.gracelogic.platform.market.model;

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
    @org.hibernate.annotations.Type(type = "pg-uuid")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DISCOUNT_ID", nullable = false)
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
}
