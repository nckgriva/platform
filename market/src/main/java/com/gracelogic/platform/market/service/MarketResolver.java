package com.gracelogic.platform.market.service;

import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.model.Product;

import java.util.List;
import java.util.UUID;

public interface MarketResolver {
    Long calculateOrderTotalAmount(UUID userId, List<Product> products) throws OrderNotConsistentException;
}
