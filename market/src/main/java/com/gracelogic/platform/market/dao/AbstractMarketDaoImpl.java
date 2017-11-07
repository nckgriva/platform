package com.gracelogic.platform.market.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.market.DataConstants;
import com.gracelogic.platform.market.model.OrderProduct;
import com.gracelogic.platform.market.model.Product;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractMarketDaoImpl extends BaseDao implements MarketDao {
    private static Logger logger = Logger.getLogger(AbstractMarketDaoImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    public Set<UUID> getProductIdsWithNullObjectReferenceIdByProductTypes(Collection<UUID> productTypeIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("productTypeIds", productTypeIds);
        params.put("active", true);
        List<Product> products = idObjectService.getList(Product.class, null, "el.productType.id in (:productTypeIds) and el.referenceObjectId is null and el.active=:active", params, null, null, null, null);
        Set<UUID> productIds = new HashSet<>();
        for (Product product : products) {
            productIds.add(product.getId());
        }

        return productIds;
    }

    private String buildCheckPurchasingQuery(boolean notEmptyProductsWithNullObjectReferenceId) {
        StringBuilder cause = new StringBuilder("ord.user.id=:userId and ord.orderState.id=:orderStateId " +
                "and (el.lifetimeExpiration is null or el.lifetimeExpiration < :checkOnDate) " +
                "and (el.product.referenceObjectId in (:referenceObjectIds) ");
        if (notEmptyProductsWithNullObjectReferenceId) {
            cause.append("or el.product.id in (:productIds))");
        } else {
            cause.append(")");
        }
        return cause.toString();
    }

    private Map<String, Object> buildCheckPurchasingParams(UUID userId, Collection<UUID> referenceObjectIds, Collection<UUID> productsWithNullObjectReferenceId, Date checkOnDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("referenceObjectIds", referenceObjectIds);
        params.put("checkOnDate", checkOnDate);
        params.put("orderStateId", DataConstants.OrderStates.PAID.getValue());
        if (!productsWithNullObjectReferenceId.isEmpty()) {
            params.put("productIds", productsWithNullObjectReferenceId);
        }

        return params;
    }

    public boolean existAtLeastOneProductIsPurchased(UUID userId, Collection<UUID> referenceObjectIds, Collection<UUID> productsWithNullObjectReferenceId, Date checkOnDate) {
        Map<String, Object> params = buildCheckPurchasingParams(userId, referenceObjectIds, productsWithNullObjectReferenceId, checkOnDate);
        return idObjectService.checkExist(OrderProduct.class, "left join el.order ord left join el.product prd",
                buildCheckPurchasingQuery(!productsWithNullObjectReferenceId.isEmpty()), params, 1) > 0;
    }

    public List<OrderProduct> getPurchasedProducts(UUID userId, Collection<UUID> referenceObjectIds, Collection<UUID> productsWithNullObjectReferenceId, Date checkOnDate) {
        Map<String, Object> params = buildCheckPurchasingParams(userId, referenceObjectIds, productsWithNullObjectReferenceId, checkOnDate);
        return idObjectService.getList(OrderProduct.class, "left join el.order ord left join fetch el.product prd",
                buildCheckPurchasingQuery(!productsWithNullObjectReferenceId.isEmpty()), params, null, null, null, null);
    }

    public List<Product> getProductsByReferenceObjectIdsAndIds(Collection<UUID> referenceObjectIds, Collection<UUID> productIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("referenceObjectIds", referenceObjectIds);

        StringBuilder cause = new StringBuilder("el.referenceObjectId in (:referenceObjectIds) ");
        if (!productIds.isEmpty()) {
            cause.append("and el.id in (:productIds) ");
            params.put("productIds", productIds);
        }
        return idObjectService.getList(Product.class, null, cause.toString(), params, null, null, null, null);
    }
}
