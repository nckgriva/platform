package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.market.model.Product;

public class PurchasedProductDTO extends ProductDTO {
    private Boolean purchased;

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public static PurchasedProductDTO prepare(Product product, Boolean purchased) {
        PurchasedProductDTO dto = new PurchasedProductDTO();
        prepare(dto, product);
        dto.setPurchased(purchased);

        return dto;
    }
}
