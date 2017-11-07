package com.gracelogic.platform.market.dao;

import com.gracelogic.platform.market.model.OrderProduct;
import com.gracelogic.platform.market.model.Product;

import java.util.*;

public interface MarketDao {
    Set<UUID> getProductIdsWithNullObjectReferenceIdByProductTypes(Collection<UUID> productTypeIds);

    boolean existAtLeastOneProductIsPurchased(UUID userId, Collection<UUID> referenceObjectIds, Collection<UUID> productsWithNullObjectReferenceId, Date checkOnDate);

    List<OrderProduct> getPurchasedProducts(UUID userId, Collection<UUID> referenceObjectIds, Collection<UUID> productsWithNullObjectReferenceId, Date checkOnDate);

    List<Product> getProductsByReferenceObjectIdsAndIds(Collection<UUID> objectReferenceIds, Collection<UUID> productIds);
}
