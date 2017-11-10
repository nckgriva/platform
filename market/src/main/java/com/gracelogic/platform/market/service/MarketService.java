package com.gracelogic.platform.market.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.market.dto.*;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.exception.ProductNotPurchasedException;
import com.gracelogic.platform.market.model.Discount;
import com.gracelogic.platform.market.model.Order;
import com.gracelogic.platform.market.model.Product;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import java.util.*;

public interface MarketService {
    Order saveOrder(OrderDTO dto, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ObjectNotFoundException, ForbiddenException, InvalidDiscountException;

    OrderExecutionParametersDTO executeOrder(UUID orderId, UUID paymentSystemId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ForbiddenException, InvalidPaymentSystemException, AccountNotFoundException, InsufficientFundsException, InvalidDiscountException, ObjectNotFoundException;

    void cancelOrder(UUID orderId) throws InvalidOrderStateException, ForbiddenException, ObjectNotFoundException, InsufficientFundsException, AccountNotFoundException;

    void processPayment(Payment payment) throws InvalidOrderStateException, AccountNotFoundException, InsufficientFundsException;

    void deleteOrder(UUID orderId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, ObjectNotFoundException, ForbiddenException;

    OrderDTO getOrder(UUID id, boolean enrich) throws ObjectNotFoundException;

    EntityListResponse<OrderDTO> getOrdersPaged(UUID userId, UUID orderStateId, UUID discountId, boolean enrich, boolean withProducts, Integer count, Integer page, Integer start, String sortField, String sortDir);

    //Product
    ProductDTO getProduct(UUID id, boolean enrich) throws ObjectNotFoundException;

    EntityListResponse<ProductDTO> getProductsPaged(String name, UUID productTypeId, Boolean active, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Product saveProduct(ProductDTO dto) throws ObjectNotFoundException;

    void deleteProduct(UUID id) throws ObjectNotFoundException;


    //Discount
    DiscountDTO getDiscount(UUID id, boolean enrich) throws ObjectNotFoundException;

    EntityListResponse<DiscountDTO> getDiscountsPaged(UUID usedForOrderId, UUID discountTypeId, boolean enrich, boolean withProducts, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Discount saveDiscount(DiscountDTO dto) throws ObjectNotFoundException;

    void deleteDiscount(UUID id) throws ObjectNotFoundException;


    void checkAtLeastOneProductPurchased(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkOnDate) throws ProductNotPurchasedException;

    Map<UUID, Boolean> getProductsPurchaseState(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkDate, Set<UUID> productIds);

    Map<UUID, Product> findProducts(Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Set<UUID> productIds);

    void enrichMarketInfo(UUID productTypeId, Collection<MarketAwareObjectDTO> objects, UUID relatedUserId, Date checkOnDate);
}