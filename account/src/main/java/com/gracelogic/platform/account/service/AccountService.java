package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.dto.TransactionDTO;
import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.db.dto.EntityListResponse;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface AccountService {
    void processTransaction(UUID accountId, UUID transactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException;

    void processTransfer(UUID sourceAccountId, UUID sourceTransactionTypeId, UUID destinationAccountId, UUID destinationTransactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException;

    EntityListResponse<TransactionDTO> getTransactionsPaged(UUID userId, UUID accountId, Collection<UUID> transactionTypeIds, Date startDate, Date endDate, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
