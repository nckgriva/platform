package com.gracelogic.platform.payment.service;

import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.payment.DataConstants;
import com.gracelogic.platform.payment.dto.CalcPaymentFeeResult;
import com.gracelogic.platform.payment.dto.ProcessPaymentRequest;
import com.gracelogic.platform.payment.exception.AccountNotFoundException;
import com.gracelogic.platform.payment.exception.PaymentAlreadyExistException;
import com.gracelogic.platform.payment.model.Payment;
import com.gracelogic.platform.payment.model.PaymentState;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private DictionaryService ds;

    @Override
    public Account checkPaymentAbility(PaymentSystem paymentSystem, String accountNumber, String currency) {
        return accountResolver.getTargetAccount(null, accountNumber, paymentSystem, currency);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment processPayment(PaymentSystem paymentSystem, ProcessPaymentRequest paymentModel) throws PaymentAlreadyExistException, AccountNotFoundException {
        if (!StringUtils.isEmpty(paymentModel.getPaymentUID())) {
            Integer count = idObjectService.checkExist(Payment.class, null, String.format("el.paymentSystem.id='%s' and el.paymentUID = '%s'", paymentSystem.getId().toString(), paymentModel.getPaymentUID()), null, 1);
            if (count > 0) {
                throw new PaymentAlreadyExistException("Payment already exist");
            }
        }

        Payment payment = new Payment();
        payment.setPaymentState(idObjectService.getObjectById(PaymentState.class, DataConstants.PaymentStates.CREATED.getValue()));
        payment.setPaymentSystem(paymentSystem);

        Account account = accountResolver.getTargetAccount(null, paymentModel.getAccountNumber(), paymentSystem, paymentModel.getCurrency());
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
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

        payment = idObjectService.save(payment);

        try {
            accountResolver.paymentReceived(payment);
            payment.setPaymentState(ds.get(PaymentState.class, DataConstants.PaymentStates.ACTIVATED.getValue()));
            idObjectService.save(payment);
        }
        catch (Exception e) {
            logger.error("Failed to transmit payment event", e);
        }

        return payment;
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
