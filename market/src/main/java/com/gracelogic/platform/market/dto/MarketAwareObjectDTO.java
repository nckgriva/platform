package com.gracelogic.platform.market.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MarketAwareObjectDTO extends IdObjectDTO implements Serializable {
    private List<PurchasedProductDTO> products = new LinkedList<>();

    public List<PurchasedProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<PurchasedProductDTO> products) {
        this.products = products;
    }

    public Boolean getPurchased() {
        if (products != null) {
            for (PurchasedProductDTO dto : products) {
                if (dto.getPurchased() == null) {
                    return null;
                }
                if (dto.getPurchased()) {
                    return true;
                }
            }
        }
        return false;
    }
}
