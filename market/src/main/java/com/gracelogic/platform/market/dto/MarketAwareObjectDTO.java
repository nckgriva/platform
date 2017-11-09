package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.market.model.Product;

import java.io.Serializable;
import java.util.UUID;

public class MarketAwareObjectDTO extends IdObjectDTO implements Serializable {
    private UUID productId;
    private String productName;
    private Long productPrice;
    private Boolean productPurchased;
    private Boolean productActive;
    private Boolean productAvailable;

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
        if (productPrice != null) {
            return FinanceUtils.toFractional2Rounded(productPrice);
        }
        else {
            return null;
        }
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

    public Boolean getProductAvailable() {
        return productAvailable;
    }

    public void setProductAvailable(Boolean productAvailable) {
        this.productAvailable = productAvailable;
    }

    public static void enrichMarketInfo(MarketAwareObjectDTO dto, Product product) {
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductPrice(product.getPrice());
        dto.setProductActive(product.getActive());
    }
}
