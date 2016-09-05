package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;

import java.util.UUID;

public interface AccountService {
    void processTransaction(UUID accountId, UUID transactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException;
}
