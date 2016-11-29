package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.IncorrectPaymentStateException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.service.AccountService;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:49
 */
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
    public Payment processPayment(UUID paymentSystemId, ProcessPaymentRequest paymentModel, AuthorizedUser executedBy) throws PaymentAlreadyExistException, AccountNotFoundException, InvalidPaymentSystemException {
        PaymentSystem paymentSystem = idObjectService.getObjectById(PaymentSystem.class, paymentSystemId);
        if (paymentSystem == null || !paymentSystem.getActive()) {
            throw new InvalidPaymentSystemException("InvalidPaymentSystemException");
        }

        if (!StringUtils.isEmpty(paymentModel.getPaymentUID())) {
            Map<String, Object> params = new HashMap<>();
            params.put("paymentSystemId", paymentSystemId);
            params.put("paymentUID", paymentModel.getPaymentUID());

            Integer count = idObjectService.checkExist(Payment.class, null, "el.paymentSystem.id=:paymentSystemId and el.paymentUID=:paymentUID", params, 1);
            if (count > 0) {
                throw new PaymentAlreadyExistException("PaymentAlreadyExistException");
            }
        }

        Payment payment = new Payment();
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.CREATED.getValue()));
        payment.setPaymentSystem(paymentSystem);

        Account account = accountResolver.getTargetAccount(null, paymentModel.getAccountNumber(), paymentSystem, paymentModel.getCurrency());
        if (account == null) {
            throw new AccountNotFoundException("AccountNotFoundException");
        }
        User user = account.getUser();

        payment.setAccount(account);
        payment.setUser(user);
        payment.setPaymentUID(paymentModel.getPaymentUID());
        payment.setDescription(paymentModel.getDescription());
        payment.setExternalTypeUID(paymentModel.getExternalTypeUID());

        if (payment.getRegisteredAmount() == null || !payment.getRegisteredAmount().equals(FinanceUtils.toDecimal(paymentModel.getRegisteredAmount()))) {
            CalcPaymentFeeResult response = calcPaymentFee(paymentSystem, paymentModel.getRegisteredAmount());

            payment.setRegisteredAmount(FinanceUtils.toDecimal(paymentModel.getRegisteredAmount()));
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
        }
        catch (InsufficientFundsException ignored) {}

        try {
            accountResolver.notifyPaymentReceived(payment);
            payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.ACTIVATED.getValue()));
            idObjectService.save(payment);
        }
        catch (Exception e) {
            logger.error("Failed to transmit payment event", e);
        }

        return payment;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelPayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException {
        Payment payment = idObjectService.getObjectById(Payment.class, paymentId);
        if (!payment.getPaymentState().getId().equals(DataConstants.PaymentStates.ACTIVATED.getValue())) {
            throw new IncorrectPaymentStateException("Payment must have state 'ACTIVATED'");
        }

        accountResolver.notifyPaymentCancelled(payment);
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.CANCELLED.getValue()));
        idObjectService.save(payment);

        try {
            accountService.processTransaction(payment.getAccount().getId(), DataConstants.TransactionTypes.CANCEL_PAYMENT.getValue(), -1 * payment.getAmount(), payment.getId(), true);
        }
        catch (InsufficientFundsException ignored) {}
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void restorePayment(UUID paymentId, AuthorizedUser executedBy) throws AccountNotFoundException, IncorrectPaymentStateException {
        Payment payment = idObjectService.getObjectById(Payment.class, paymentId);
        if (!payment.getPaymentState().getId().equals(DataConstants.PaymentStates.CANCELLED.getValue())) {
            throw new IncorrectPaymentStateException("Payment must have state 'CANCELLED'");
        }
        accountResolver.notifyPaymentRestored(payment);
        payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.ACTIVATED.getValue()));
        idObjectService.save(payment);

        try {
            accountService.processTransaction(payment.getAccount().getId(), DataConstants.TransactionTypes.RESTORE_PAYMENT.getValue(), payment.getAmount(), payment.getId(), true);
        }
        catch (InsufficientFundsException ignored) {}
    }

    @Override
    public CalcPaymentFeeResult calcPaymentFee(PaymentSystem paymentSystem, Double registeredAmount) {
        CalcPaymentFeeResult response = new CalcPaymentFeeResult();

        Double amount;
        Double fee ;
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
}
