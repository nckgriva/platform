package com.gracelogic.platform.onec.service;

import com.gracelogic.platform.finance.FinanceUtils;
import com.gracelogic.platform.onec.dto.BankPayment;
import com.gracelogic.platform.onec.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class OneCServiceImpl implements OneCService {

    private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public List<BankPayment> importBankPayments(String source, boolean ignoreExceptions) throws OneCParsingException {
        LinkedList<BankPayment> payments = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(source, "\n");
        int line = 0;
        BankPayment bankPayment = null;
        while (st.hasMoreTokens()) {
            line++;
            String current = st.nextToken();

            if (current.startsWith("СекцияДокумент")) {
                bankPayment = new BankPayment();
            }
            if (current.startsWith("КонецДокумента")) {
                if (bankPayment != null) {
                    payments.addLast(bankPayment);
                    bankPayment = null;
                }
            }
            if (bankPayment != null) {
                try {
                    process(bankPayment, current, ignoreExceptions);
                }
                catch (OneCParsingException e) {
                    e.setLineNumber(line);
                    throw e;
                }
            }
        }

        return payments;
    }

    public static void process(BankPayment bankPayment, String s, boolean ignoreExceptions) throws OneCParsingException {
        String expression = s.substring(0, s.indexOf("=") + 1);
        String value = s.substring(s.indexOf("=") + 1).trim();

        switch (expression) {
            case "ПолучательИНН=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions)  {
                    throw new InnFieldEmptyException();
                }
                bankPayment.getRecipientBankAccount().getOrganization().setInn(value);
                break;
            case "ПолучательКПП=":
                bankPayment.getRecipientBankAccount().getOrganization().setKpp(value);
                break;
            case "Получатель=":
                bankPayment.getRecipientBankAccount().getOrganization().setName(value);
                break;
            case "Получатель1=":
                bankPayment.getRecipientBankAccount().getOrganization().setName(value);
                break;
            case "ПолучательОГРН=":
                bankPayment.getRecipientBankAccount().getOrganization().setOgrn(value);
                break;
            case "ПолучательРасчСчет=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new RaschAccountFieldEmptyException();
                }
                bankPayment.getRecipientBankAccount().setRaschAccount(value);
                break;
            case "ПолучательСчет=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new RaschAccountFieldEmptyException();
                }
                bankPayment.getRecipientBankAccount().setRaschAccount(value);
                break;

            case "ПолучательБанк1=":
                bankPayment.getRecipientBankAccount().setBankName(value);
                break;
            case "ПолучательБИК=":
                bankPayment.getRecipientBankAccount().setBik(value);
                break;
            case "ПолучательКорсчет=":
                bankPayment.getRecipientBankAccount().setCorrAccount(value);
                break;


            case "ПлательщикИНН=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new InnFieldEmptyException();
                }
                bankPayment.getPayerBankAccount().getOrganization().setInn(value);
                break;
            case "ПлательщикКПП=":
                bankPayment.getPayerBankAccount().getOrganization().setKpp(value);
                break;
            case "Плательщик=":
                bankPayment.getPayerBankAccount().getOrganization().setName(value);
                break;
            case "Плательщик1=":
                bankPayment.getPayerBankAccount().getOrganization().setName(value);
                break;
            case "ПлательщикОГРН=":
                bankPayment.getPayerBankAccount().getOrganization().setOgrn(value);
                break;
            case "ПлательщикРасчСчет=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new RaschAccountFieldEmptyException();
                }
                bankPayment.getPayerBankAccount().setRaschAccount(value);
                break;
            case "ПлательщикСчет=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new RaschAccountFieldEmptyException();
                }
                bankPayment.getPayerBankAccount().setRaschAccount(value);
                break;
            case "ПлательщикБанк1=":
                bankPayment.getPayerBankAccount().setBankName(value);
                break;
            case "ПлательщикБИК=":
                bankPayment.getPayerBankAccount().setBik(value);
                break;
            case "ПлательщикКорсчет=":
                bankPayment.getPayerBankAccount().setCorrAccount(value);
                break;

            case "Сумма=":
                if (StringUtils.isEmpty(value) && !ignoreExceptions) {
                    throw new SumFieldEmptyException();
                }
                value = value.replaceAll(",", ".");
                bankPayment.setAmount(Double.parseDouble(value));
                break;
            case "Дата=":
                if (StringUtils.isEmpty(value)) {
                    bankPayment.setDateIsNull(true);
                } else {
                    try {
                        Date date = dateFormat.parse(value);
                        bankPayment.setCreateDate(date);
                    }
                    catch (ParseException ignored) {}
                }
                break;
            case "ДатаПоступило=":
                if (!StringUtils.isEmpty(value)) {
                    try {
                        Date date = dateFormat.parse(value);
                        bankPayment.setIncomingDate(date);
                    } catch (ParseException e) {
                        if (!ignoreExceptions) {
                            throw new AllDateIsNullException();
                        }
                    }
                }
                else if (bankPayment.isDateIsNull() && !ignoreExceptions) {
                    throw new AllDateIsNullException();
                }
                break;
            case "ДатаСписано=":
                if (!StringUtils.isEmpty(value)) {
                    try {
                        Date date = dateFormat.parse(value);
                        bankPayment.setOutgoingDate(date);
                    }
                    catch (ParseException e) {
                        if (!ignoreExceptions) {
                            throw new AllDateIsNullException();
                        }
                    }
                }
                else if (bankPayment.isDateIsNull() && !ignoreExceptions) {
                    throw new AllDateIsNullException();
                }
                break;
            case "НазначениеПлатежа=":
                bankPayment.setDescription(value);
                break;
            case "НазначениеПлатежа1=":
                bankPayment.setDescription(value);
                break;
            case "Номер=":
                bankPayment.setNumber(value);
                break;

        }
    }

    @Override
    public String exportBankPayments(List<BankPayment> payments) {
        StringBuilder sb = new StringBuilder();
        sb.append("1CClientBankExchange\n" +
                "ВерсияФормата=1.03\n" +
                "Кодировка=Windows\n" +
                "Отправитель=Контур.Бухгалтерия\n");
        Date startDate = null;
        Date endDate = null;
        String accountNumber = null;
        for (BankPayment payment : payments) {
            if (startDate == null) {
                startDate = payment.getCreateDate();
            }
            else if (startDate.getTime() > payment.getCreateDate().getTime()) {
                startDate = payment.getCreateDate();
            }
            if (endDate == null) {
                endDate = payment.getCreateDate();
            }
            else if (endDate.getTime() < payment.getCreateDate().getTime()) {
                endDate = payment.getCreateDate();
            }
            if (StringUtils.isEmpty(accountNumber) && payment.getPayerBankAccount() != null) {
                accountNumber = payment.getPayerBankAccount().getRaschAccount();
            }
        }

        if (startDate != null && endDate != null && !StringUtils.isEmpty(accountNumber)) {
            sb.append("ДатаНачала=" + dateFormat.format(startDate) + "\n" +
                    "ДатаКонца=" + dateFormat.format(endDate) + "\n" +
                    "РасчСчет=" + accountNumber + "\n");
        }

        for (BankPayment payment : payments) {
            sb.append("СекцияДокумент=Платежное поручение\n");

            sb.append("Номер=" + payment.getNumber() + "\n");
            sb.append("Дата=" + dateFormat.format(payment.getCreateDate()) + "\n");
            sb.append("Сумма=" + FinanceUtils.round(payment.getAmount(), 2) + "\n");

            if (payment.getPayerBankAccount() != null && payment.getPayerBankAccount().getOrganization() != null) {
                sb.append("ПлательщикСчет=" + payment.getPayerBankAccount().getRaschAccount() + "\n");
                sb.append("Плательщик=" + payment.getPayerBankAccount().getOrganization().getInn() + " " + payment.getPayerBankAccount().getOrganization().getName() + "\n");
                sb.append("ПлательщикИНН=" + payment.getPayerBankAccount().getOrganization().getInn() + "\n");
                sb.append("ПлательщикКПП=" + payment.getPayerBankAccount().getOrganization().getKpp() + "\n");
                sb.append("Плательщик1=" + payment.getPayerBankAccount().getOrganization().getName() + "\n");
                sb.append("ПлательщикРасчСчет=" + payment.getPayerBankAccount().getRaschAccount() + "\n");
                sb.append("ПлательщикБанк1=" + payment.getPayerBankAccount().getBankName() + "\n");
                sb.append("ПлательщикБанк2=" + payment.getPayerBankAccount().getBankCity() + "\n");
                sb.append("ПлательщикБИК=" + payment.getPayerBankAccount().getBik() + "\n");
                sb.append("ПлательщикКорсчет=" + payment.getPayerBankAccount().getCorrAccount() + "\n");
            }
            if (payment.getRecipientBankAccount() != null && payment.getRecipientBankAccount().getOrganization() != null) {
                sb.append("ПолучательСчет=" + payment.getRecipientBankAccount().getRaschAccount() + "\n");
                sb.append("Получатель=" + payment.getRecipientBankAccount().getOrganization().getInn() + " " + payment.getPayerBankAccount().getOrganization().getName() + "\n");
                sb.append("ПолучательИНН=" + payment.getRecipientBankAccount().getOrganization().getInn() + "\n");
                sb.append("ПолучательКПП=" + payment.getRecipientBankAccount().getOrganization().getKpp() + "\n");
                sb.append("Получатель1=" + payment.getRecipientBankAccount().getOrganization().getName() + "\n");
                sb.append("ПолучательРасчСчет=" + payment.getRecipientBankAccount().getRaschAccount() + "\n");
                sb.append("ПолучательБанк1=" + payment.getRecipientBankAccount().getBankName() + "\n");
                sb.append("ПолучательБанк2=" + payment.getRecipientBankAccount().getBankCity() + "\n");
                sb.append("ПолучательБИК=" + payment.getRecipientBankAccount().getBik() + "\n");
                sb.append("ПолучательКорсчет=" + payment.getRecipientBankAccount().getCorrAccount() + "\n");
            }
            sb.append("ВидПлатежа=" + "\n");
            sb.append("ВидОплаты=01" + "\n");
            sb.append("НазначениеПлатежа=" + payment.getDescription() + "\n");
            sb.append("Очередность=5" + "\n");
            sb.append("КонецДокумента" + "\n");
        }
        sb.append("КонецФайла" + "\n");

        return sb.toString();
    }
}
