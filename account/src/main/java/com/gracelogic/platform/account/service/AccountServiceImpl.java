package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.model.Transaction;
import com.gracelogic.platform.account.model.TransactionType;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processTransaction(UUID accountId, UUID transactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException {
        Account account = idObjectService.getObjectById(Account.class, accountId);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (amount < 0 && !ignoreInsufficientFunds && account.getBalance() < Math.abs(amount)) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(account.getBalance());
        transaction.setTransactionType(ds.get(TransactionType.class, transactionTypeId));
        transaction.setReferenceObjectId(referenceObjectId);

        account.setBalance(account.getBalance() + amount);
        account = idObjectService.save(account);

        transaction.setBalanceAfter(account.getBalance());
        idObjectService.save(transaction);
    }
}
