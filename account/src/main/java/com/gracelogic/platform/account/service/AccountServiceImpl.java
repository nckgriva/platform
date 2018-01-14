package com.gracelogic.platform.account.service;

import com.gracelogic.platform.account.dto.AccountDTO;
import com.gracelogic.platform.account.dto.TransactionDTO;
import com.gracelogic.platform.account.exception.AccountNotFoundException;
import com.gracelogic.platform.account.exception.CurrencyMismatchException;
import com.gracelogic.platform.account.exception.InsufficientFundsException;
import com.gracelogic.platform.account.exception.NoActualExchangeRateException;
import com.gracelogic.platform.account.model.Account;
import com.gracelogic.platform.account.model.ExchangeRate;
import com.gracelogic.platform.account.model.Transaction;
import com.gracelogic.platform.account.model.TransactionType;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.finance.FinanceUtils;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processTransfer(UUID sourceAccountId, UUID sourceTransactionTypeId, UUID destinationAccountId, UUID destinationTransactionTypeId, Long amount, UUID referenceObjectId, boolean ignoreInsufficientFunds) throws InsufficientFundsException, AccountNotFoundException, CurrencyMismatchException {
        Account sourceAccount = idObjectService.getObjectById(Account.class, sourceAccountId);
        Account destinationAccount = idObjectService.getObjectById(Account.class, destinationAccountId);

        if (sourceAccount == null || destinationAccount == null) {
            throw new AccountNotFoundException("AccountNotFoundException");
        }
        if (!sourceAccount.getCurrency().getId().equals(destinationAccount.getCurrency().getId())) {
            throw new CurrencyMismatchException();
        }

        processTransaction(sourceAccountId, sourceTransactionTypeId, -1 * amount, referenceObjectId, ignoreInsufficientFunds);
        processTransaction(destinationAccountId, destinationTransactionTypeId, amount, referenceObjectId, ignoreInsufficientFunds);
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

    @Override
    public EntityListResponse<AccountDTO> getAccountsPaged(UUID accountTypeId, UUID currencyId, UUID userId, String externalIdentifier, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.user left join fetch el.accountType left join fetch el.currency" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(externalIdentifier)) {
            params.put("externalIdentifier", "%%" + StringUtils.lowerCase(externalIdentifier) + "%%");
            cause += "and lower(el.externalIdentifier) like :externalIdentifier ";
        }

        if (accountTypeId != null) {
            cause += "and el.accountType.id = :accountTypeId ";
            params.put("accountTypeId", accountTypeId);
        }

        if (currencyId != null) {
            cause += "and el.currency.id = :currencyId ";
            params.put("currencyId", currencyId);
        }

        if (userId != null) {
            cause += "and el.user.id = :userId ";
            params.put("userId", userId);
        }

        int totalCount = idObjectService.getCount(Account.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<AccountDTO> entityListResponse = new EntityListResponse<AccountDTO>();
        entityListResponse.setEntity("account");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Account> items = idObjectService.getList(Account.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Account e : items) {
            AccountDTO el = AccountDTO.prepare(e);
            if (enrich) {
                AccountDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
    
    @Override
    public AccountDTO getAccount(UUID id, boolean enrich) throws ObjectNotFoundException {
        Account entity = idObjectService.getObjectById(Account.class, enrich ? "left join fetch el.user left join fetch el.accountType left join fetch el.currency" : "",id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        AccountDTO dto = AccountDTO.prepare(entity);
        if (enrich) {
            AccountDTO.enrich(dto, entity);
        }
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAccount(UUID id) {
        idObjectService.delete(Account.class, id);
    }

    @Override
    public ExchangeRate getActualExchangeRate(UUID sourceCurrencyId, UUID destinationCurrencyId, Date validOnDate) throws NoActualExchangeRateException {
        Map<String, Object> params = new HashMap<>();
        params.put("sourceCurrencyId", sourceCurrencyId);
        params.put("destinationCurrencyId", destinationCurrencyId);
        if (validOnDate == null) {
            validOnDate = new Date();
        }
        params.put("validOnDate", validOnDate);

        List<ExchangeRate> rates = idObjectService.getList(ExchangeRate.class, null, "el.sourceCurrency.id=:sourceCurrencyId and el.destinationCurrency.id=:destinationCurrencyId and (el.lifetimeExpiration is null or el.lifetimeExpiration > :validOnDate)", params, "el.created", "DESC", null, 1);
        if (rates.isEmpty()) {
            throw new NoActualExchangeRateException();
        }
        else {
            return rates.iterator().next();
        }
    }

    @Override
    public Long translateAmountInOtherCurrency(UUID sourceCurrencyId, Long amount, UUID destinationCurrencyId) throws NoActualExchangeRateException {
        ExchangeRate exchangeRate = getActualExchangeRate(sourceCurrencyId, destinationCurrencyId, null);
        return FinanceUtils.toDecimal(FinanceUtils.toFractional(amount) * FinanceUtils.toFractional(exchangeRate.getValue()));
    }
}
