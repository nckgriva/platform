package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.market.model.Product;

import java.util.UUID;

public class MarketAwareObjectDTO extends IdObjectDTO {
    private UUID productId;
    private String productName;
    private Long productPrice;
    private Boolean productPurchased;
    private Boolean productActive;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public Double getProductPriceAsFractional() {
        return FinanceUtils.toFractional2Rounded(productPrice);
    }

    public Boolean getProductPurchased() {
        return productPurchased;
    }

    public void setProductPurchased(Boolean productPurchased) {
        this.productPurchased = productPurchased;
    }

    public Boolean getProductActive() {
        return productActive;
    }

    public void setProductActive(Boolean productActive) {
        this.productActive = productActive;
    }

    public static void enrichMarketInfo(MarketAwareObjectDTO dto, Product product) {
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductPrice(product.getPrice());
        dto.setProductActive(product.getActive());
    }
}
