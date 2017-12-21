package com.gracelogic.platform.market.service;


import com.gracelogic.platform.market.model.Order;

public interface MarketResolver {
    void orderPaid(Order order);
}
