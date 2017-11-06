package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
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
    private Long totalAmount;
    private Long paid;
    private UUID discountId;
    private String discountName;
    private List<ProductDTO> products = new LinkedList<>();

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
        dto.setTotalAmount(model.getTotalAmount());
        dto.setPaid(model.getPaid());

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

        return dto;
    }
}
