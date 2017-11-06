package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.dto.TransactionDTO;
import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.model.Transaction;
import com.gracelogic.platform.account.model.TransactionType;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
            throw new AccountNotFoundException("AccountNotFoundException");
        }

        if (amount < 0 && !ignoreInsufficientFunds && account.getBalance() < Math.abs(amount)) {
            throw new InsufficientFundsException("Insufficient funds in account: " + accountId);
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

    @Override
    public EntityListResponse<TransactionDTO> getTransactionsPaged(UUID userId, UUID accountId, Collection<UUID> transactionTypeIds, Date startDate, Date endDate, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "left join fetch el.account acc left join fetch acc.user usr left join fetch el.transactionType ttp";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (accountId != null) {
            cause += "and el.account.id=:accountId ";
            params.put("accountId", accountId);
        }
        if (userId != null) {
            cause += "and el.account.user.id=:userId ";
            params.put("userId", userId);
        }
        if (startDate != null) {
            cause += "and el.created >= :startDate ";
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            cause += "and el.created <= :endDate ";
            params.put("endDate", endDate);
        }
        if (transactionTypeIds != null && !transactionTypeIds.isEmpty()) {
            cause += "and el.transactionType.id in (:transactionTypeIds) ";
            params.put("transactionTypeIds", transactionTypeIds);
        }

        int totalCount = idObjectService.getCount(Transaction.class, null, null, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<TransactionDTO> entityListResponse = new EntityListResponse<TransactionDTO>();
        entityListResponse.setEntity("transaction");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Transaction> items = idObjectService.getList(Transaction.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Transaction e : items) {
            TransactionDTO el = TransactionDTO.prepare(e);
            if (enrich) {
                TransactionDTO.enrich(el, e);
            }

            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
