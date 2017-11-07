package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.IncorrectPaymentStateException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
import com.gracelogic.platform.payment.dto.PaymentDTO;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.InvalidPaymentSystemException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentState;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static Logger logger = Logger.getLogger(PaymentServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private AccountResolver accountResolver;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DictionaryService ds;

    @Override
    public Account checkPaymentAbility(UUID paymentSystemId, String accountNumber, String currency) throws InvalidPaymentSystemException, AccountNotFoundException {
        PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
        if (paymentSystem == null || !paymentSystem.getActive()) {
            throw new InvalidPaymentSystemException("InvalidPaymentSystemException");
        }

        return accountResolver.getTargetAccount(null, accountNumber, paymentSystem, currency);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment processPayment(UUID paymentSystemId, ProcessPaymentRequest paymentRequest, AuthorizedUser executedBy) throws PaymentAlreadyExistException, AccountNotFoundException, InvalidPaymentSystemException {
        PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
        if (paymentSystem == null || !paymentSystem.getActive()) {
            throw new InvalidPaymentSystemException("InvalidPaymentSystemException");
        }

        if (!StringUtils.isEmpty(paymentRequest.getPaymentUID())) {
            Map<String, Object> params = new HashMap<>();
            params.put("paymentSystemId", paymentSystemId);
            params.put("paymentUID", paymentRequest.getPaymentUID());

            Integer count = idObjectService.checkExist(Payment.class, null, "el.paymentSystem.id=:paymentSystemId and el.paymentUID=:paymentUID", params, 1);
            if (count > 0) {
                throw new PaymentAlreadyExistException("PaymentAlreadyExistException");
            }
        }

        Payment payment = new Payment();
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.CREATED.getValue()));
        payment.setPaymentSystem(paymentSystem);
        payment.setAccountNumber(paymentRequest.getAccountNumber());

        Account account = null;
        if (paymentRequest.getAccountId() != null) {
            account = idObjectService.getObjectById(Account.class, paymentRequest.getAccountId());
        } else {
            account = accountResolver.getTargetAccount(null, paymentRequest.getAccountNumber(), paymentSystem, paymentRequest.getCurrency());
        }

        if (account == null) {
            throw new AccountNotFoundException("AccountNotFoundException");
        }

        payment.setAccount(account);
        payment.setPaymentUID(paymentRequest.getPaymentUID());
        payment.setDescription(paymentRequest.getDescription());
        payment.setExternalTypeUID(paymentRequest.getExternalTypeUID());

        if (payment.getRegisteredAmount() == null || !payment.getRegisteredAmount().equals(FinanceUtils.toDecimal(paymentRequest.getRegisteredAmount()))) {
            CalcPaymentFeeResult response = calcPaymentFee(paymentSystem, paymentRequest.getRegisteredAmount());

            payment.setRegisteredAmount(FinanceUtils.toDecimal(paymentRequest.getRegisteredAmount()));
            payment.setAmount(FinanceUtils.toDecimal(response.getAmount()));
            payment.setFee(FinanceUtils.toDecimal(response.getFee()));
            payment.setTotalAmount(FinanceUtils.toDecimal(response.getTotalAmount()));
        }

        if (executedBy != null) {
            payment.setExecutedByUser(idObjectService.getObjectById(User.class, executedBy.getId()));
        }

        payment = idObjectService.save(payment);

        try {
            accountService.processTransaction(account.getId(), DataConstants.TransactionTypes.INCOMING_PAYMENT.getValue(), payment.getAmount(), payment.getId(), true);
        } catch (InsufficientFundsException ignored) {
        }

        try {
            accountResolver.notifyPaymentReceived(payment);
            payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.ACTIVATED.getValue()));
            idObjectService.save(payment);
        } catch (Exception e) {
            logger.error("Failed to transmit payment event", e);
        }

        return payment;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelPayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException, InsufficientFundsException {
        Payment payment = idObjectService.getObjectById(Payment.class, paymentId);
        if (!payment.getPaymentState().getId().equals(DataConstants.PaymentStates.ACTIVATED.getValue())) {
            throw new IncorrectPaymentStateException("Payment must have state 'ACTIVATED'");
        }

        accountResolver.notifyPaymentCancelled(payment);
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.CANCELLED.getValue()));
        idObjectService.save(payment);

        accountService.processTransaction(payment.getAccount().getId(), DataConstants.TransactionTypes.CANCEL_PAYMENT.getValue(), -1 * payment.getAmount(), payment.getId(), false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void restorePayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException, InsufficientFundsException {
        Payment payment = idObjectService.getObjectById(Payment.class, paymentId);
        if (!payment.getPaymentState().getId().equals(DataConstants.PaymentStates.CANCELLED.getValue())) {
            throw new IncorrectPaymentStateException("Payment must have state 'CANCELLED'");
        }
        accountResolver.notifyPaymentRestored(payment);
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.ACTIVATED.getValue()));
        idObjectService.save(payment);

        accountService.processTransaction(payment.getAccount().getId(), DataConstants.TransactionTypes.INCOMING_PAYMENT.getValue(), payment.getAmount(), payment.getId(), false);
    }

    @Override
    public CalcPaymentFeeResult calcPaymentFee(PaymentSystem paymentSystem, Double registeredAmount) {
        CalcPaymentFeeResult response = new CalcPaymentFeeResult();

        Double amount;
        Double fee;
        Double totalAmount;
        if (paymentSystem.getFeeIncluded()) {
            amount = FinanceUtils.round(registeredAmount / (1D + FinanceUtils.toFractional(paymentSystem.getFee()) / 100D), 2);
            fee = registeredAmount - amount;
            totalAmount = registeredAmount;
        } else {
            amount = registeredAmount;
            fee = FinanceUtils.round(registeredAmount * FinanceUtils.toFractional(paymentSystem.getFee()) / 100D, 2);
            totalAmount = registeredAmount;
        }
        response.setAmount(amount);
        response.setFee(fee);
        response.setTotalAmount(totalAmount);
        return response;
    }

    @Override
    public EntityListResponse<PaymentDTO> getPaymentsPaged(UUID userId, UUID accountId, UUID paymentSystemId, Collection<UUID> paymentStateIds, Date startDate, Date endDate, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String cause = "1=1 ";
        String countFetches = "";
        String fetches = enrich ? "left join fetch el.account acc left join fetch acc.user usr left join fetch el.paymentSystem pss left join fetch el.paymentState pst" : null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (userId != null) {
            cause += "and el.user.id=:userId ";
            params.put("userId", userId);
        }
        if (accountId != null) {
            cause += "and el.account.id=:accountId ";
            params.put("accountId", accountId);
        }
        if (paymentSystemId != null) {
            cause += "and el.paymentSystem.id=:paymentSystemId ";
            params.put("paymentSystemId", paymentSystemId);
        }
        if (paymentStateIds != null && !paymentStateIds.isEmpty()) {
            cause += "and el.paymentState.id in (:paymentStateIds) ";
            params.put("paymentStateIds", paymentStateIds);
        }
        if (startDate != null) {
            cause += "and el.created >= :startDate ";
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            cause += "and el.created <= :endDate ";
            params.put("endDate", endDate);
        }

        int totalCount = idObjectService.getCount(Payment.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<PaymentDTO> entityListResponse = new EntityListResponse<PaymentDTO>();
        entityListResponse.setEntity("payment");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Payment> items = idObjectService.getList(Payment.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Payment e : items) {
            PaymentDTO el = PaymentDTO.prepare(e);
            if (enrich) {
                PaymentDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
