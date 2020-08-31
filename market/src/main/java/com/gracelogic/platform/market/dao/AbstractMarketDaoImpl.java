package com.gracelogic.platform.market.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.market.DataConstants;
import com.gracelogic.platform.market.model.OrderProduct;
import com.gracelogic.platform.market.model.Product;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractMarketDaoImpl extends BaseDao implements MarketDao {

    @Autowired
    private IdObjectService idObjectService;

    private String buildCheckPurchasingQuery() {
        return "ord.ownerId=:ownerId and ord.orderState.id=:orderStateId " +
                "and (el.lifetimeExpiration is null or el.lifetimeExpiration >= :checkOnDate) " +
                "and el.product.referenceObjectId in (:referenceObjectIds) ";
    }

    private Map<String, Object> buildCheckPurchasingParams(UUID ownerId, Collection<UUID> referenceObjectIds, Date checkOnDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("ownerId", ownerId);
        params.put("referenceObjectIds", referenceObjectIds);
        params.put("checkOnDate", checkOnDate);
        params.put("orderStateId", DataConstants.OrderStates.PAID.getValue());

        return params;
    }

    public boolean existAtLeastOneProductIsPurchased(UUID ownerId, Collection<UUID> referenceObjectIds, Date checkOnDate) {
        Map<String, Object> params = buildCheckPurchasingParams(ownerId, referenceObjectIds, checkOnDate);
        return idObjectService.checkExist(OrderProduct.class, "left join el.order ord left join el.product prd",
                buildCheckPurchasingQuery(), params, 1) > 0;
    }

    public List<OrderProduct> getPurchasedProducts(UUID ownerId, Collection<UUID> referenceObjectIds, Date checkOnDate) {
        Map<String, Object> params = buildCheckPurchasingParams(ownerId, referenceObjectIds, checkOnDate);
        return idObjectService.getList(OrderProduct.class, "left join el.order ord left join fetch el.product prd",
                buildCheckPurchasingQuery(), params, null, null, null, null);
    }

    public List<Product> getProductsByReferenceObjectIds(Collection<UUID> referenceObjectIds, boolean onlyPrimary) {
        Map<String, Object> params = new HashMap<>();
        params.put("referenceObjectIds", referenceObjectIds);
        String cause = "el.referenceObjectId in (:referenceObjectIds) ";
        if (onlyPrimary) {
            cause += "and el.primary=true";
        }

        return idObjectService.getList(Product.class, null, cause, params, "el.primary", "ASC", null, null);
    }
}
