package com.gracelogic.platform.market.dao;

import com.gracelogic.platform.market.model.OrderProduct;
import com.gracelogic.platform.market.model.Product;

import java.util.*;

public interface MarketDao {
    boolean existAtLeastOneProductIsPurchased(UUID userId, Collection<UUID> referenceObjectIds, Date checkOnDate);

    List<OrderProduct> getPurchasedProducts(UUID userId, Collection<UUID> referenceObjectIds, Date checkOnDate);

    List<Product> getProductsByReferenceObjectIds(Collection<UUID> referenceObjectIds);
}
