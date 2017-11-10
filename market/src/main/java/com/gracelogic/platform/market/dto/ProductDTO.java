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

    public static ProductDTO prepare(Product model) {
        ProductDTO dto = new ProductDTO();
        IdObjectDTO.prepare(dto, model);

        if (model.getProductType() != null) {
            dto.setProductTypeId(model.getProductType().getId());
        }
        dto.setName(model.getName());
        dto.setActive(model.getActive());
        dto.setReferenceObjectId(model.getReferenceObjectId());
        return dto;
    }

    public static ProductDTO enrich(ProductDTO dto, Product model) {
        if (model.getProductType() != null) {
            dto.setProductTypeName(model.getProductType().getName());
        }

        return dto;
    }
}
