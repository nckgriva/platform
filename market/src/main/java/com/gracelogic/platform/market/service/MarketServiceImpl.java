package com.gracelogic.platform.market.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.market.DataConstants;
import com.gracelogic.platform.market.dao.MarketDao;

import com.gracelogic.platform.payment.dto.PaymentExecutionResultDTO;
import com.gracelogic.platform.market.dto.*;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.exception.InvalidOrderStateException;
import com.gracelogic.platform.market.exception.OrderNotConsistentException;
import com.gracelogic.platform.market.exception.ProductNotPurchasedException;
import com.gracelogic.platform.market.model.*;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentExecutionException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.payment.service.AccountResolver;
import com.gracelogic.platform.payment.service.PaymentExecutor;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
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

    @Autowired
    private ApplicationContext applicationContext;

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
            entity.setPaid(0L);
        }

        Set<UUID> productIds = new HashSet<>();
        Set<UUID> discountProductIds = new HashSet<>();
        //Find discount
        Discount discount = null;
        if (!StringUtils.isEmpty(dto.getDiscountSecretCode())) {
            discount = getActiveDiscountBySecretCode(dto.getDiscountSecretCode());
            if (discount == null) {
                throw new InvalidDiscountException();
            }
            if (discount.getDiscountType().getId().equals(DataConstants.DiscountTypes.GIFT_PRODUCT.getValue())) {
                Map<String, Object> params = new HashMap<>();
                params.put("discountId", discount.getId());
                List<DiscountProduct> discountProducts = idObjectService.getList(DiscountProduct.class, null, "el.discount.id=:discountId", params, null, null, null, null);
                for (DiscountProduct discountProduct : discountProducts) {
                    discountProductIds.add(discountProduct.getProduct().getId());
                }
            }
        }

        //Get products to purchase
        productIds.addAll(discountProductIds);
        for (ProductDTO productDTO : dto.getProducts()) {
            productIds.add(productDTO.getId());
        }

        if (productIds.isEmpty()) {
            throw new OrderNotConsistentException("No products found in order");
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
                List<Product> onlyDiscountedProducts = new LinkedList<>();
                for (Product product : products) {
                    if (discountProductIds.contains(product.getId())) {
                        onlyDiscountedProducts.add(product);
                    }
                }
                discountAmount = marketResolver.calculateOrderTotalAmount(entity.getUser().getId(), onlyDiscountedProducts);
            }
        }

        Long totalAmount = amount - discountAmount;
        entity.setAmount(amount);
        entity.setDiscountAmount(discountAmount);
        entity.setTotalAmount(totalAmount);
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

        List<Discount> discounts = idObjectService.getList(Discount.class, null, "el.secretCode=:secretCode and el.active=:active and ((el.reusable=false and el.used=false) or el.reusable=true)", params, null, null, null, 1);
        return discounts.isEmpty() ? null : discounts.iterator().next();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentExecutionResultDTO executeOrder(UUID orderId, UUID paymentSystemId, Map<String, String> params, AuthorizedUser authorizedUser) throws InvalidOrderStateException, OrderNotConsistentException, ForbiddenException, InvalidPaymentSystemException, AccountNotFoundException, InsufficientFundsException, InvalidDiscountException, ObjectNotFoundException, PaymentExecutionException {
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

        if (order.getOrderState().getId().equals(DataConstants.OrderStates.DRAFT.getValue())) {
            order.setOrderState(ds.get(OrderState.class, DataConstants.OrderStates.PENDING.getValue()));
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


        //Пытаемся оплатить с помощью внутреннего счёта
        Account userAccount = accountResolver.getTargetAccount(order.getUser(), null, null, null);
        Long amountToPay = order.getTotalAmount() - order.getPaid();
        if (userAccount.getBalance() >= amountToPay) {
            order = payOrder(order, amountToPay, userAccount.getId());
            amountToPay = order.getTotalAmount() - order.getPaid();
        }

        //Всё что недоплачено пытаемся получить через платёжную систему
        if (order.getOrderState().getId().equals(DataConstants.OrderStates.PENDING.getValue())) {
            PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
            if (paymentSystem == null || !paymentSystem.getActive() || StringUtils.isEmpty(paymentSystem.getPaymentExecutorClass())) {
                throw new InvalidPaymentSystemException();
            }

            if (order.getPaymentSystem() == null || !order.getPaymentSystem().getId().equals(paymentSystemId)) {
                order.setPaymentSystem(paymentSystem);
                idObjectService.save(paymentSystem);
            }

            try {
                PaymentExecutor paymentExecutor = initializePaymentExecutor(paymentSystem.getPaymentExecutorClass());
                return paymentExecutor.execute(String.valueOf(order.getId()), amountToPay, applicationContext, params);
            } catch (PaymentExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new PaymentExecutionException(e.getMessage());
            }
        }

        return new PaymentExecutionResultDTO(order.getOrderState().getId().equals(DataConstants.OrderStates.PAID.getValue()));
    }

    private PaymentExecutor initializePaymentExecutor(String paymentExecutorClassName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName(paymentExecutorClassName);
        return (PaymentExecutor) clazz.newInstance();
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
        Account userAccount = accountResolver.getTargetAccount(order.getUser(), null, null, null);

        accountService.processTransfer(propertyService.getPropertyValueAsUUID("market:organization_account_id"), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_SELL_CANCEL.getValue(),
                userAccount.getId(), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_BUY_CANCEL.getValue(),
                amountToReturn, order.getId(), false);

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
        if (!marketDao.existAtLeastOneProductIsPurchased(userId, referenceObjectIdsAndProductTypeIds.keySet(), checkOnDate)) {
            throw new ProductNotPurchasedException();
        }
    }

    @Override
    public Map<UUID, Boolean> getProductsPurchaseState(UUID userId, Map<UUID, UUID> referenceObjectIdsAndProductTypeIds, Date checkDate) {
        Map<UUID, Boolean> result = new HashMap<>();

        List<OrderProduct> orderProducts = marketDao.getPurchasedProducts(userId, referenceObjectIdsAndProductTypeIds.keySet(), checkDate);

        for (UUID referenceObjectId : referenceObjectIdsAndProductTypeIds.keySet()) {
            UUID productTypeId = referenceObjectIdsAndProductTypeIds.get(referenceObjectId);
            boolean found = false;
            for (OrderProduct orderProduct : orderProducts) {
                if (orderProduct.getProduct().getReferenceObjectId() != null
                        && orderProduct.getProduct().getReferenceObjectId().equals(referenceObjectId)
                        && orderProduct.getProduct().getProductType().getId().equals(productTypeId)) {
                    found = true;
                    break;
                }
            }
            result.put(referenceObjectId, found);
        }

        return result;
    }

    public Map<UUID, Product> findProducts(Map<UUID, UUID> referenceObjectIdsAndProductTypeIds) {
        Map<UUID, Product> result = new HashMap<>();
        List<Product> products = marketDao.getProductsByReferenceObjectIds(referenceObjectIdsAndProductTypeIds.keySet());

        for (UUID referenceObjectId : referenceObjectIdsAndProductTypeIds.keySet()) {
            UUID productTypeId = referenceObjectIdsAndProductTypeIds.get(referenceObjectId);
            Product p = null;
            for (Product product : products) {
                if (product.getReferenceObjectId() != null
                        && product.getReferenceObjectId().equals(referenceObjectId)
                        && product.getProductType().getId().equals(productTypeId)) {
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

        Map<UUID, Boolean> purchased = Collections.emptyMap();
        if (relatedUserId != null) {
            purchased = getProductsPurchaseState(relatedUserId, referenceObjectIdsAndProductTypeIds, checkOnDate);
        }

        Map<UUID, Product> products = findProducts(referenceObjectIdsAndProductTypeIds);

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
        accountService.processTransfer(userAccountId, com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_BUY.getValue(),
                propertyService.getPropertyValueAsUUID("market:organization_account_id"), com.gracelogic.platform.payment.DataConstants.TransactionTypes.MARKET_SELL.getValue(),
                amountToPay, order.getId(), false);

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

    @Override
    public OrderDTO getOrder(UUID id, boolean enrich) throws ObjectNotFoundException {
        Order entity = idObjectService.getObjectById(Order.class, enrich ? "left join fetch el.user left join fetch el.orderState left join fetch el.discount" : "", id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        OrderDTO dto = OrderDTO.prepare(entity);
        if (enrich) {
            OrderDTO.enrich(dto, entity);
        }
        return dto;
    }

    @Override
    public EntityListResponse<OrderDTO> getOrdersPaged(UUID userId, UUID orderStateId, UUID discountId, boolean enrich, boolean withProducts, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.user left join fetch el.orderState left join fetch el.discount" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (userId != null) {
            cause += "and el.user.id=:userId ";
            params.put("userId", userId);
        }

        if (orderStateId != null) {
            cause += "and el.orderState.id=:orderStateId ";
            params.put("orderStateId", orderStateId);
        }

        if (discountId != null) {
            cause += "and el.discount.id=:discountId ";
            params.put("discontId", discountId);
        }

        int totalCount = idObjectService.getCount(Order.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<OrderDTO> entityListResponse = new EntityListResponse<OrderDTO>();
        entityListResponse.setEntity("order");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Order> items = idObjectService.getList(Order.class, fetches, cause, params, sortField, sortDir, startRecord, count);

        List<OrderProduct> orderProducts = Collections.emptyList();
        if (withProducts && !items.isEmpty()) {
            Set<UUID> orderIds = new HashSet<>();
            for (Order ord : items) {
                orderIds.add(ord.getId());
            }
            Map<String, Object> productParams = new HashMap<>();
            productParams.put("orderIds", orderIds);
            orderProducts = idObjectService.getList(OrderProduct.class, null, "el.order.id in (:orderIds)", productParams, null, null, null, null);
        }

        entityListResponse.setPartCount(items.size());
        for (Order e : items) {
            OrderDTO el = OrderDTO.prepare(e);
            if (enrich) {
                OrderDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public ProductDTO getProduct(UUID id, boolean enrich) throws ObjectNotFoundException {
        Product entity = idObjectService.getObjectById(Product.class, enrich ? "left join fetch el.productType" : "", id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        ProductDTO dto = ProductDTO.prepare(entity);
        if (enrich) {
            ProductDTO.enrich(dto, entity);
        }
        return dto;
    }

    @Override
    public EntityListResponse<ProductDTO> getProductsPaged(String name, UUID productTypeId, Boolean active, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.productType" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += "and lower(el.name) like :name";
        }
        if (productTypeId != null) {
            params.put("productTypeId", productTypeId);
            cause += "and el.productType.id = :productTypeId ";
        }
        if (active != null) {
            params.put("active", active);
            cause += "and el.active = :active ";
        }
        int totalCount = idObjectService.getCount(Product.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<ProductDTO> entityListResponse = new EntityListResponse<ProductDTO>();
        entityListResponse.setEntity("product");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Product> items = idObjectService.getList(Product.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Product e : items) {
            ProductDTO el = ProductDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public Product saveProduct(ProductDTO dto) throws ObjectNotFoundException {
        Product entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Product.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Product();
        }

        entity.setName(dto.getName());
        entity.setActive(dto.getActive());
        entity.setProductType(idObjectService.getObjectById(ProductType.class, dto.getProductTypeId()));
        entity.setReferenceObjectId(dto.getReferenceObjectId());
        entity.setLifetime(dto.getLifetime());
        entity.setPrice(dto.getPrice());

        return idObjectService.save(entity);
    }

    @Override
    public void deleteProduct(UUID id) throws ObjectNotFoundException {
        idObjectService.delete(Product.class, id);
    }

    @Override
    public DiscountDTO getDiscount(UUID id, boolean enrich) throws ObjectNotFoundException {
        Discount entity = idObjectService.getObjectById(Discount.class, enrich ? "left join fetch el.productType left join fetch el.usedForOrder" : "", id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        DiscountDTO dto = DiscountDTO.prepare(entity);
        if (enrich) {
            DiscountDTO.enrich(dto, entity);
        }
        return dto;
    }

    @Override
    public EntityListResponse<DiscountDTO> getDiscountsPaged(UUID usedForOrderId, UUID discountTypeId, boolean enrich, boolean withProducts, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.usedForOrder left join fetch el.discountType" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (usedForOrderId != null) {
            cause += "and el.usedForOrder.id=:usedForOrderId ";
            params.put("usedForOrderId", usedForOrderId);
        }

        if (discountTypeId != null) {
            cause += "and el.discountType.id=:discountTypeId ";
            params.put("discountTypeId", discountTypeId);
        }

        int totalCount = idObjectService.getCount(Discount.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<DiscountDTO> entityListResponse = new EntityListResponse<DiscountDTO>();
        entityListResponse.setEntity("discount");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Discount> items = idObjectService.getList(Discount.class, fetches, cause, params, sortField, sortDir, startRecord, count);

        List<DiscountProduct> discountProducts = Collections.emptyList();
        if (withProducts && !items.isEmpty()) {
            Set<UUID> discountIds = new HashSet<>();
            for (Discount dis: items) {
                discountIds.add(dis.getId());
            }
            Map<String, Object> productParams = new HashMap<>();
            productParams.put("discountIds", discountIds);
            discountProducts = idObjectService.getList(DiscountProduct.class, null, "el.discount.id in (:discountIds)", productParams, null, null, null, null);
        }

        entityListResponse.setPartCount(items.size());
        for (Discount e : items) {
            DiscountDTO el = DiscountDTO.prepare(e);
            if (enrich) {
                DiscountDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public Discount saveDiscount(DiscountDTO dto) throws ObjectNotFoundException {
        Discount entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Discount.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Discount();
        }

        if (entity.getId() != null) {
            String query = "el.discount.id=:discountId";
            HashMap<String, Object> params = new HashMap<>();
            params.put("discountId", entity.getId());
            idObjectService.delete(DiscountProduct.class, query, params);
        }

        entity.setName(dto.getName());
        entity.setActive(dto.getActive());
        entity.setReusable(dto.getReusable());
        entity.setUsed(dto.getUsed());
        entity.setUsedForOrder(idObjectService.getObjectById(Order.class, dto.getUsedForOrderId()));
        entity.setDiscountType(idObjectService.getObjectById(DiscountType.class, dto.getDiscountTypeId()));
        entity.setSecretCode(dto.getSecretCode());
        entity.setAmount(dto.getAmount());
        idObjectService.save(entity);

        for (ProductDTO productDTO : dto.getProducts()) {
            DiscountProduct dp = new DiscountProduct();
            dp.setDiscount(entity);
            dp.setProduct(idObjectService.getObjectById(Product.class, productDTO.getId()));
            idObjectService.save(dp);
        }

        return entity;
    }

    @Override
    public void deleteDiscount(UUID id) throws ObjectNotFoundException {
        Map<String, Object> params = new HashMap<>();
        params.put("discountId", id);
        idObjectService.delete(DiscountProduct.class, "el.discount.id=:discountId", params);
        idObjectService.delete(Discount.class, id);
    }
}
