package com.gracelogic.platform.market.dao;

import com.gracelogic.platform.market.model.OrderProduct;
import com.gracelogic.platform.market.model.Product;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MarketDao {
    boolean existAtLeastOneProductIsPurchased(UUID userId, Collection<UUID> referenceObjectIds, Date checkOnDate);

    List<OrderProduct> getPurchasedProducts(UUID userId, Collection<UUID> referenceObjectIds, Date checkOnDate);

    List<Product> getProductsByReferenceObjectIds(Collection<UUID> referenceObjectIds, boolean onlyPrimary);
}
