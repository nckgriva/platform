package com.gracelogic.platform.market.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.market.model.Product;

import java.util.Date;
import java.util.UUID;

public class ProductDTO extends IdObjectDTO {
    private String name;
    private UUID referenceObjectId;
    private UUID productTypeId;
    private String productTypeName;
    private Boolean active;
    private Long lifetime;
    private Long price;
    private Boolean primary;
    private UUID currencyId;
    private String currencyName;
    private UUID ownershipTypeId;
    private String ownershipTypeName;

    //Transient value
    private Date lifetimeExpiration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    public UUID getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(UUID productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


    public Long getLifetime() { return lifetime; }

    public void setLifetime(Long lifetime) { this.lifetime = lifetime; }

    public Long getPrice() { return price; }

    public void setPrice(Long price) { this.price = price; }

    public Double getPriceAsFractional() {
        if (this.price != null) {
            return FinanceUtils.toFractional2Rounded(this.price);
        }
        else {
            return null;
        }
    }

    public void setPriceAsFractional(String sPrice) {
        this.price = FinanceUtils.stringToLong(sPrice);
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
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

    public UUID getOwnershipTypeId() {
        return ownershipTypeId;
    }

    public void setOwnershipTypeId(UUID ownershipTypeId) {
        this.ownershipTypeId = ownershipTypeId;
    }

    public String getOwnershipTypeName() {
        return ownershipTypeName;
    }

    public void setOwnershipTypeName(String ownershipTypeName) {
        this.ownershipTypeName = ownershipTypeName;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getLifetimeExpiration() {
        return lifetimeExpiration;
    }

    public void setLifetimeExpiration(Date lifetimeExpiration) {
        this.lifetimeExpiration = lifetimeExpiration;
    }

    public static ProductDTO prepare(Product model) {
        ProductDTO dto = new ProductDTO();
        return prepare(dto, model);
    }

    public static ProductDTO prepare(ProductDTO dto, Product model) {
        IdObjectDTO.prepare(dto, model);

        if (model.getProductType() != null) {
            dto.setProductTypeId(model.getProductType().getId());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyId(model.getCurrency().getId());
        }
        if (model.getOwnershipType() != null) {
            dto.setOwnershipTypeId(model.getOwnershipType().getId());
        }
        dto.setName(model.getName());
        dto.setActive(model.getActive());
        dto.setReferenceObjectId(model.getReferenceObjectId());
        dto.setLifetime(model.getLifetime());
        dto.setPrice(model.getPrice());
        dto.setPrimary(model.getPrimary());
        return dto;
    }

    public static ProductDTO enrich(ProductDTO dto, Product model) {
        if (model.getProductType() != null) {
            dto.setProductTypeName(model.getProductType().getName());
        }
        if (model.getCurrency() != null) {
            dto.setCurrencyName(model.getCurrency().getName());
        }
        if (model.getOwnershipType() != null) {
            dto.setOwnershipTypeName(model.getOwnershipType().getName());
        }

        return dto;
    }
}
