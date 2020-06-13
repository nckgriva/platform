package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.dto.AccountDTO;
import com.gracelogic.platform.account.dto.TransactionDTO;
import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.CurrencyMismatchException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.exception.NoActualExchangeRateException;
import com.gracelogic.platform.account.model.ExchangeRate;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface AccountService {
    void processTransaction(UUID accountId, UUID transactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException;

    void processTransfer(UUID sourceAccountId, UUID sourceTransactionTypeId, UUID destinationAccountId, UUID destinationTransactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException, CurrencyMismatchException;

    EntityListResponse<TransactionDTO> getTransactionsPaged(UUID userId, UUID accountId, Collection<UUID> transactionTypeIds, Date startDate, Date endDate, boolean enrich, boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir);
    
    EntityListResponse<AccountDTO> getAccountsPaged(UUID accountTypeId, UUID currencyId, UUID userId, String externalIdentifier, boolean enrich, boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir);

    AccountDTO getAccount(UUID id, boolean enrich) throws ObjectNotFoundException;

    void deleteAccount(UUID id);

    ExchangeRate getActualExchangeRate(UUID sourceCurrencyId, UUID destinationCurrencyId, Date date) throws NoActualExchangeRateException;

    Long translateAmountInOtherCurrency(UUID sourceCurrencyId, Long amount, UUID destinationCurrencyId) throws NoActualExchangeRateException;
}
