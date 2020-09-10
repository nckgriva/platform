package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.user.dto.UserDTO;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class OrderDTO extends IdObjectDTO {
    private UUID userId;
    private String userName;
    private UUID orderStateId;
    private String orderStateName;
    private Long amount;
    private Long discountAmount;
    private Long totalAmount;
    private Long paid;
    private UUID discountId;
    private String discountName;
    private List<ProductDTO> products = new LinkedList<>();
    private String externalIdentifier;
    private UUID paymentSystemId;
    private String paymentSystemName;
    private UUID targetCurrencyId;
    private String targetCurrencyName;
    private Long periodicity;
    private UUID ownershipTypeId;
    private String ownershipName;
    private UUID parentOrderId;
    private UUID ownerId;
    private Boolean subscriptionCancelled;

    //Transient value for user order creation
    private String discountSecretCode;

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

    public UUID getOrderStateId() {
        return orderStateId;
    }

    public void setOrderStateId(UUID orderStateId) {
        this.orderStateId = orderStateId;
    }

    public String getOrderStateName() {
        return orderStateName;
    }

    public void setOrderStateName(String orderStateName) {
        this.orderStateName = orderStateName;
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

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountSecretCode() {
        return discountSecretCode;
    }

    public void setDiscountSecretCode(String discountSecretCode) {
        this.discountSecretCode = discountSecretCode;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
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

    public UUID getPaymentSystemId() {
        return paymentSystemId;
    }

    public void setPaymentSystemId(UUID paymentSystemId) {
        this.paymentSystemId = paymentSystemId;
    }

    public String getPaymentSystemName() {
        return paymentSystemName;
    }

    public void setPaymentSystemName(String paymentSystemName) {
        this.paymentSystemName = paymentSystemName;
    }

    public Double getAmountAsFractional() {
        if (this.amount != null) {
            return FinanceUtils.toFractional2Rounded(this.amount);
        }
        else {
            return null;
        }
    }

    public void setAmountAsFractional(String sValue) {
        this.amount = FinanceUtils.stringToLong(sValue);
    }

    public Double getDiscountAmountAsFractional() {
        if (this.discountAmount != null) {
            return FinanceUtils.toFractional2Rounded(this.discountAmount);
        }
        else {
            return null;
        }
    }

    public void setDiscountAmountAsFractional(String sValue) {
        this.discountAmount = FinanceUtils.stringToLong(sValue);
    }

    public Double getTotalAmountAsFractional() {
        if (this.totalAmount != null) {
            return FinanceUtils.toFractional2Rounded(this.totalAmount);
        }
        else {
            return null;
        }
    }

    public void setTotalAmountAsFractional(String sValue) {
        this.totalAmount = FinanceUtils.stringToLong(sValue);
    }

    public Double getPaidAsFractional() {
        if (this.paid != null) {
            return FinanceUtils.toFractional2Rounded(this.paid);
        }
        else {
            return null;
        }
    }

    public void setPaidAsFractional(String sValue) {
        this.paid = FinanceUtils.stringToLong(sValue);
    }

    public UUID getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(UUID targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public String getTargetCurrencyName() {
        return targetCurrencyName;
    }

    public void setTargetCurrencyName(String targetCurrencyName) {
        this.targetCurrencyName = targetCurrencyName;
    }

    public Long getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Long periodicity) {
        this.periodicity = periodicity;
    }

    public UUID getOwnershipTypeId() {
        return ownershipTypeId;
    }

    public void setOwnershipTypeId(UUID ownershipTypeId) {
        this.ownershipTypeId = ownershipTypeId;
    }

    public String getOwnershipName() {
        return ownershipName;
    }

    public void setOwnershipName(String ownershipName) {
        this.ownershipName = ownershipName;
    }

    public UUID getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(UUID parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getSubscriptionCancelled() {
        return subscriptionCancelled;
    }

    public void setSubscriptionCancelled(Boolean subscriptionCancelled) {
        this.subscriptionCancelled = subscriptionCancelled;
    }

    public static OrderDTO prepare(Order model) {
        OrderDTO dto = new OrderDTO();
        IdObjectDTO.prepare(dto, model);

        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId());
        }
        if (model.getOrderState() != null) {
            dto.setOrderStateId(model.getOrderState().getId());
        }
        if (model.getDiscount() != null) {
            dto.setDiscountId(model.getDiscount().getId());
        }
        if (model.getPaymentSystem() != null) {
            dto.setPaymentSystemId(model.getPaymentSystem().getId());
        }
        if (model.getTargetCurrency() != null) {
            dto.setTargetCurrencyId(model.getTargetCurrency().getId());
        }
        if (model.getOwnershipType() != null) {
            dto.setOwnershipTypeId(model.getOwnershipType().getId());
        }
        if (model.getParentOrder() != null) {
            dto.setParentOrderId(model.getParentOrder().getId());
        }
        dto.setTotalAmount(model.getTotalAmount());
        dto.setPaid(model.getPaid());
        dto.setAmount(model.getAmount());
        dto.setDiscountAmount(model.getDiscountAmount());
        dto.setPeriodicity(model.getPeriodicity());
        dto.setExternalIdentifier(model.getExternalIdentifier());
        dto.setOwnerId(model.getOwnerId());
        dto.setSubscriptionCancelled(model.getSubscriptionCancelled());

        return dto;
    }

    public static OrderDTO enrich(OrderDTO dto, Order model) {
        if (model.getUser() != null) {
            dto.setUserName(UserDTO.formatUserName(model.getUser()));
        }
        if (model.getOrderState() != null) {
            dto.setOrderStateName(model.getOrderState().getName());
        }
        if (model.getDiscount() != null) {
            dto.setDiscountName(model.getDiscount().getName());
        }
        if (model.getPaymentSystem() != null) {
            dto.setPaymentSystemName(model.getPaymentSystem().getName());
        }
        if (model.getTargetCurrency() != null) {
            dto.setTargetCurrencyName(model.getTargetCurrency().getName());
        }

        return dto;
    }
}
