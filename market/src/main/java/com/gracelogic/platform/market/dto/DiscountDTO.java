package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.market.model.Discount;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DiscountDTO extends IdObjectDTO {
    private String name;
    private Boolean active;
    private Boolean reusable;
    private Boolean used; //Актуально только для случая с reusable = false
    private UUID usedForOrderId; //Актуально только для случая с reusable = false
    private UUID discountTypeId;
    private String discountTypeName;
    private String secretCode;
    private Long amount;
    private UUID currencyId;
    private String currencyName;
    private List<ProductDTO> products = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getReusable() { return reusable; }

    public void setReusable(Boolean reusable) {
        this.reusable = reusable;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public UUID getUsedForOrderId() { return usedForOrderId; }

    public void setUsedForOrderId(UUID usedForOrderId) { this.usedForOrderId = usedForOrderId; }

    public UUID getDiscountTypeId() { return discountTypeId; }

    public void setDiscountTypeId(UUID discountTypeId) { this.discountTypeId = discountTypeId; }

    public String getDiscountTypeName() { return discountTypeName; }

    public void setDiscountTypeName(String discountTypeName) { this.discountTypeName = discountTypeName; }

    public String getSecretCode() { return secretCode; }

    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }

    public Long getAmount() { return amount; }

    public Double getAmountAsFractional() {
        if (this.amount != null) {
            return FinanceUtils.toFractional2Rounded(this.amount);
        }
        else {
            return null;
        }
    }

    public void setAmountAsFractional(String sAmount) {
        this.amount = FinanceUtils.stringToLong(sAmount);
    }

    public void setAmount(Long amount) { this.amount = amount; }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
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

    public static DiscountDTO prepare(Discount model) {
        DiscountDTO dto = new DiscountDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setName(model.getName());
        dto.setActive(model.getActive());
        dto.setReusable(model.getReusable());
        dto.setUsed(model.getUsed());
        if (model.getUsedForOrder() != null) {
            dto.setUsedForOrderId(model.getUsedForOrder().getId());
        }
        if (model.getDiscountType() != null) {
            dto.setDiscountTypeId(model.getDiscountType().getId());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyId(model.getCurrency().getId());
        }
        dto.setSecretCode(model.getSecretCode());
        dto.setAmount(model.getAmount());

        return dto;
    }
    
    public static DiscountDTO enrich(DiscountDTO dto, Discount model) {
        if (model.getDiscountType() != null) {
            dto.setDiscountTypeName(model.getDiscountType().getName());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyName(model.getCurrency().getName());
        }
        return dto;
    }
}
