package com.gracelogic.platform.onec.service;

import com.gracelogic.platform.onec.dto.BankPayment;
import com.gracelogic.platform.onec.exception.AllDateIsNullException;
import com.gracelogic.platform.onec.exception.InnFieldEmptyException;
import com.gracelogic.platform.onec.exception.RaschAccountFieldEmptyException;
import com.gracelogic.platform.onec.exception.SumFieldEmptyException;

import java.util.List;

public interface OneCService {
    List<BankPayment> importBankPayments(String source, boolean ignoreExceptions) throws InnFieldEmptyException, RaschAccountFieldEmptyException, SumFieldEmptyException, AllDateIsNullException;

    String exportBankPayments(List<BankPayment> payments);
}
