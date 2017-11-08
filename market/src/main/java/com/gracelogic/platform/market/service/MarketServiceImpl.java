package com.gracelogic.platform.market.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.market.DataConstants;
import com.gracelogic.platform.market.dao.MarketDao;
import com.gracelogic.platform.market.dto.MarketAwareObjectDTO;
import com.gracelogic.platform.market.dto.OrderDTO;
import com.gracelogic.platform.market.dto.OrderExecutionParametersDTO;
import com.gracelogic.platform.market.dto.ProductDTO;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.exception.ProductNotPurchasedException;
import com.gracelogic.platform.market.model.*;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.payment.service.AccountResolver;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MarketServiceImpl implements MarketService {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountResolver accountResolver;

    @Autowired
    private MarketResolver marketResolver;

    @Autowired
    private MarketDao marketDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order saveOrder(OrderDTO dto, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ObjectNotFoundException, ForbiddenException, InvalidDiscountException {
        Order entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Order.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
            if (!authorizedUser.getGrants().contains("ORDER:SAVE") && !entity.getUser().getId().equals(authorizedUser.getId())) {
                throw new ForbiddenException();
            }
            if (!entity.getOrderState().getId().equals(DataConstants.OrderStates.DRAFT.getValue())) {
                throw new InvalidOrderStateException();
            }

            //Delete order products
            Map<String, Object> params = new HashMap<>();
            params.put("orderId", entity.getId());
            idObjectService.delete(OrderProduct.class, "el.order.id=:orderId", params);
        } else {
            entity = new Order();
            entity.setOrderState(ds.get(OrderState.class, DataConstants.OrderStates.DRAFT.getValue()));
            entity.setUser(idObjectService.getObjectById(User.class, authorizedUser.getId()));
        }

        Set<UUID> productIds = new HashSet<>();

        //Find discount
        Discount discount = null;
        if (!StringUtils.isEmpty(dto.getDiscountSecretCode())) {
            discount = getActiveDiscountBySecretCode(dto.getDiscountSecretCode());
            if (discount == null) {
                throw new InvalidDiscountException();
            }
            if (discount.getDiscountType().getId().equals(DataConstants.DiscountTypes.GIFT_PRODUCT.getValue())) {
                for (DiscountProduct discountProduct : discount.getDiscountProductSet()) {
                    productIds.add(discountProduct.getProduct().getId());
                }
            }
        }

        //Get products to purchase
        if (productIds.isEmpty()) {
            for (ProductDTO productDTO : dto.getProducts()) {
                productIds.add(productDTO.getId());
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("productIds", productIds);
        List<Product> products = idObjectService.getList(Product.class, null, "el.id in (:productIds)", params, null, null, null, productIds.size());

        //Calculate total amount
        Long amount = marketResolver.calculateOrderTotalAmount(entity.getUser().getId(), products);
        Long discountAmount = 0L;

        //Apply discount
        if (discount != null) {
            if (discount.getDiscountType().getId().equals(DataConstants.DiscountTypes.FIX_AMOUNT_DISCOUNT.getValue())) {
                if (discount.getAmount() == null) {
                    throw new InvalidDiscountException();
                }
                discountAmount = discount.getAmount();
                if (discountAmount > amount) {
                    discountAmount = amount;
                }
            } else if (discount.getDiscountType().getId().equals(DataConstants.DiscountTypes.FIX_PERCENT_DISCOUNT.getValue())) {
                if (discount.getAmount() == null) {
                    throw new InvalidDiscountException();
                }
                discountAmount -= amount / 100L * discount.getAmount();
            } else if (discount.getDiscountType().getId().equals(DataConstants.DiscountTypes.GIFT_PRODUCT.getValue())) {
                discountAmount = amount;
            }
        }

        Long totalAmount = amount - discountAmount;
        entity.setAmount(amount);
        entity.setDiscountAmount(discountAmount);
        entity.setTotalAmount(totalAmount);
        entity.setPaid(0L);
        entity = idObjectService.save(entity);

        //Create order products
        for (UUID productId : productIds) {
            Product product = null;
            for (Product p : products) {
                if (p.getId().equals(productId)) {
                    product = p;
                    break;
                }
            }
            if (product == null || !product.getActive()) {
                throw new OrderNotConsistentException("Product not found or not active: " + productId);
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(entity);
            orderProduct.setProduct(product);
            idObjectService.save(orderProduct);
        }

        return entity;
    }

    private Discount getActiveDiscountBySecretCode(String secretCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("secretCode", StringUtils.trim(secretCode));
        params.put("active", true);

        List<Discount> discounts = idObjectService.getList(Discount.class, "left join fetch el.discountProductSet", "el.secretCode=:secretCode and el.active=:active and ((el.reusable == false && el.executed == false) || el.reusable == true)", params, null, null, null, 1);
        return discounts.isEmpty() ? null : discounts.iterator().next();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderExecutionParametersDTO executeOrder(UUID orderId, UUID paymentSystemId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ForbiddenException, InvalidPaymentSystemException, AccountNotFoundException, InsufficientFundsException, InvalidDiscountException, ObjectNotFoundException {
        Order order = idObjectService.getObjectById(Order.class, orderId);
        if (order == null) {
            throw new ObjectNotFoundException();
        }

        if (!authorizedUser.getGrants().contains("ORDER:EXECUTE") && !order.getUser().getId().equals(authorizedUser.getId())) {
            throw new ForbiddenException();
        }

        if (!order.getOrderState().getId().equals(DataConstants.OrderStates.DRAFT.getValue()) &&
                !order.getOrderState().getId().equals(DataConstants.OrderStates.PENDING.getValue())) {
            throw new InvalidOrderStateException();
        }

        PaymentSystem paymentSystem = null;
        if (order.getOrderState().getId().equals(DataConstants.OrderStates.DRAFT.getValue())) {
            paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
            if (paymentSystem == null || !paymentSystem.getActive()) {
                throw new InvalidPaymentSystemException();
            }

            order.setOrderState(ds.get(OrderState.class, DataConstants.OrderStates.PENDING.getValue()));
            order.setPaymentSystem(paymentSystem);
            order = idObjectService.save(order);

            //Check and process discount
            if (order.getDiscount() != null) {
                Discount discount = idObjectService.getObjectById(Discount.class, order.getDiscount().getId());
                if (!discount.getReusable()) {
                    if (discount.getUsed()) {
                        throw new InvalidDiscountException("This discount already used");
                    } else {
                        discount.setUsed(true);
                        discount.setUsedForOrder(order);
                        idObjectService.save(discount);
                    }
                }
            }

            //Update lifetime expiration
            recalculateOrderProductLifetimeExpiration(order, System.currentTimeMillis());
        }

        if (paymentSystem == null) {
            paymentSystem = idObjectService.getObjectById(PaymentSystem.class, order.getPaymentSystem().getId());
        }

        //Пытаемся оплатить с помощью внутреннего счёта
        Account userAccount = accountResolver.getTargetAccount(order.getUser(), null, null, null);
        if (order.getTotalAmount().equals(order.getPaid()) || userAccount.getBalance() >= (order.getTotalAmount() - order.getPaid())) {
            order = payOrder(order, order.getTotalAmount(), userAccount.getId());
        }

        return new OrderExecutionParametersDTO(order.getOrderState().getId().equals(DataConstants.OrderStates.PAID.getValue()), paymentSystem.getRedirectUrl());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrder(UUID orderId) throws InvalidOrderStateException, ForbiddenException, ObjectNotFoundException, InsufficientFundsException, AccountNotFoundException {
        Order order = idObjectService.getObjectById(Order.class, orderId);
        if (order == null) {
            throw new ObjectNotFoundException();
        }
        if (!order.getOrderState().getId().equals(DataConstants.OrderStates.PAID.getValue())) {
            throw new InvalidOrderStateException();
        }

        Long amountToReturn = order.getPaid();

        //Transfer money from organization to user
        accountService.processTransaction(propertyService.getPropertyValueAsUUID("market:organization_account_id"), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_SELL_CANCEL.getValue(), -1 * amountToReturn, order.getId(), false);
        Account userAccount = accountResolver.getTargetAccount(order.getUser(), null, null, null);
        accountService.processTransaction(userAccount.getId(), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_BUY_CANCEL.getValue(), amountToReturn, order.getId(), false);

        order.setPaid(order.getPaid() - amountToReturn);
        order.setOrderState(ds.get(OrderState.class, DataConstants.OrderStates.CANCELED.getValue()));
        idObjectService.save(order);

        //Return discount
        if (order.getDiscount() != null) {
            Discount discount = idObjectService.getObjectById(Discount.class, order.getDiscount().getId());
            if (!discount.getReusable() && discount.getUsed() && discount.getUsedForOrder().getId().equals(orderId)) {
                discount.setUsed(false);
                discount.setUsedForOrder(null);
                idObjectService.save(discount);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processPayment(Payment payment) throws InvalidOrderStateException, AccountNotFoundException, InsufficientFundsException {
        Order order = idObjectService.getObjectById(Order.class, payment.getAccountNumber());
        if (order != null && order.getOrderState().getId().equals(DataConstants.OrderStates.PENDING.getValue())) {
            Long amountToPay = order.getTotalAmount() - order.getPaid();
            if (amountToPay > payment.getAmount()) {
                amountToPay = payment.getAmount();
            }

            //Transfer money from user to organization
            payOrder(order, amountToPay, payment.getAccount().getId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteOrder(UUID orderId, AuthorizedUser authorizedUser) throws InvalidOrderStateException, ObjectNotFoundException, ForbiddenException {
        Order order = idObjectService.getObjectById(Order.class, orderId);
        if (!authorizedUser.getGrants().contains("ORDER:DELETE") && !order.getUser().getId().equals(authorizedUser.getId())) {
            throw new ForbiddenException();
        }
        if (!order.getOrderState().getId().equals(DataConstants.OrderStates.DRAFT.getValue())) {
            throw new InvalidOrderStateException();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        idObjectService.delete(OrderProduct.class, "el.order.id=:orderId", params);
        idObjectService.delete(Order.class, orderId);
    }

    @Override
    public void checkAtLeastOneProductPurchased(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkOnDate) throws ProductNotPurchasedException {
        Set<UUID> productIds = marketDao.getProductIdsWithNullObjectReferenceIdByProductTypes(referenceObjectIdsAndProductTypeIds.values());
        if (!marketDao.existAtLeastOneProductIsPurchased(userId, referenceObjectIdsAndProductTypeIds.keySet(), productIds, checkOnDate)) {
            throw new ProductNotPurchasedException();
        }
    }

    @Override
    public Map<UUID, Boolean> getProductsPurchaseState(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkDate, Set<UUID> productIds) {
        Map<UUID, Boolean> result = new HashMap<>();
        if (productIds == null) {
            productIds = marketDao.getProductIdsWithNullObjectReferenceIdByProductTypes(referenceObjectIdsAndProductTypeIds.values());
        }
        List<OrderProduct> orderProducts = marketDao.getPurchasedProducts(userId, referenceObjectIdsAndProductTypeIds.keySet(), productIds, checkDate);

        for (UUID referenceObjectId : referenceObjectIdsAndProductTypeIds.keySet()) {
            UUID productTypeId = referenceObjectIdsAndProductTypeIds.get(referenceObjectId);
            boolean found = false;
            for (OrderProduct orderProduct : orderProducts) {
                if ((orderProduct.getProduct().getReferenceObjectId() != null && orderProduct.getProduct().getReferenceObjectId().equals(referenceObjectId)) ||
                        (orderProduct.getProduct().getReferenceObjectId() == null && orderProduct.getProduct().getProductType().getId().equals(productTypeId))) {
                    found = true;
                    break;
                }
            }
            result.put(referenceObjectId, found);
        }

        return result;
    }

    public Map<UUID, Product> findProducts(Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Set<UUID> productIds) {
        Map<UUID, Product> result = new HashMap<>();
        if (productIds == null) {
            productIds = marketDao.getProductIdsWithNullObjectReferenceIdByProductTypes(referenceObjectIdsAndProductTypeIds.values());
        }
        List<Product> products = marketDao.getProductsByReferenceObjectIdsAndIds(referenceObjectIdsAndProductTypeIds.keySet(), productIds);

        for (UUID referenceObjectId : referenceObjectIdsAndProductTypeIds.keySet()) {
            UUID productTypeId = referenceObjectIdsAndProductTypeIds.get(referenceObjectId);
            Product p = null;
            for (Product product : products) {
                if ((product.getReferenceObjectId() != null && product.getReferenceObjectId().equals(referenceObjectId)) ||
                        (product.getReferenceObjectId() == null && product.getProductType().getId().equals(productTypeId))) {
                    p = product;
                    break;
                }
            }
            result.put(referenceObjectId, p);
        }

        return result;
    }

    public void enrichMarketInfo(UUID productTypeId, Collection<MarketAwareObjectDTO> objects, UUID relatedUserId, Date checkOnDate) {
        Map<UUID, UUID> referenceObjectIdsAndProductTypeIds = new HashMap<>();
        for (MarketAwareObjectDTO dto : objects) {
            if (dto.getId() == null) {
                continue;
            }
            referenceObjectIdsAndProductTypeIds.put(dto.getId(), productTypeId);
        }

        Set<UUID> productIds = marketDao.getProductIdsWithNullObjectReferenceIdByProductTypes(Collections.singletonList(productTypeId));

        Map<UUID, Boolean> purchased = Collections.emptyMap();
        if (relatedUserId != null) {
            purchased = getProductsPurchaseState(relatedUserId, referenceObjectIdsAndProductTypeIds, checkOnDate, productIds);
        }

        Map<UUID, Product> products = findProducts(referenceObjectIdsAndProductTypeIds, productIds);

        for (MarketAwareObjectDTO dto : objects) {
            if (dto.getId() == null) {
                continue;
            }
            if (products.containsKey(dto.getId())) {
                Product product = products.get(dto.getId());
                if (product != null) {
                    MarketAwareObjectDTO.enrichMarketInfo(dto, product);
                    if (purchased.containsKey(dto.getId())) {
                        dto.setProductPurchased(purchased.get(dto.getId()));
                    }
                }
            }
        }
    }

    private Order payOrder(Order order, Long amountToPay, UUID userAccountId) throws InsufficientFundsException, AccountNotFoundException {
        //Transfer money from user to organization
        accountService.processTransaction(userAccountId, com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_BUY.getValue(), -1 * amountToPay, order.getId(), false);
        accountService.processTransaction(propertyService.getPropertyValueAsUUID("market:organization_account_id"), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_SELL.getValue(), amountToPay, order.getId(), false);

        order.setPaid(order.getPaid() + amountToPay);
        if (order.getPaid().equals(order.getTotalAmount())) {
            order.setOrderState(ds.get(OrderState.class, DataConstants.OrderStates.PAID.getValue()));
        }
        return idObjectService.save(order);
    }

    private void recalculateOrderProductLifetimeExpiration(Order order, Long currentTimeMillis) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", order.getId());

        List<OrderProduct> orderProducts = idObjectService.getList(OrderProduct.class, "left join fetch el.product", "el.order.id=:orderId", params, null, null, null, null);
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getProduct().getLifetime() != null) {
                orderProduct.setLifetimeExpiration(new Date(currentTimeMillis + orderProduct.getProduct().getLifetime()));
            } else {
                orderProduct.setLifetimeExpiration(null);
            }
            idObjectService.save(orderProduct);
        }
    }
}
