package com.gracelogic.platform.market.service;


import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.user.dto.AuthorizedUser;

public interface MarketResolver {
    void orderPaid(Order order);

    /**
     * Called before deleting related records in the database
     * @param order Order
     */
    void orderDeleted(Order order, AuthorizedUser user);
}
