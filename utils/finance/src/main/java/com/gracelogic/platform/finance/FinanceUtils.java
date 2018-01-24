package com.gracelogic.platform.finance;


import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.*;

public class FinanceUtils {

    public static double calcLoanRate(double totalDebtAmount, double totalDebtInterestRateSum, int term) {
        return (totalDebtInterestRateSum * 100D / (totalDebtAmount - totalDebtInterestRateSum)) / (double) term;
    }
    public static Long stringToLong(String str) {
        double la = 0;
        if (!StringUtils.isEmpty(str)) {
            str = str.replace(',', '.');
            la = Double.parseDouble(str);
        }
        return FinanceUtils.toDecimal(la);
    }

    public static List<MonthlyPayment> calculateBorrowerPaymentList(double interestRate, int loanTerm, int delay, double amount, Date issueDate) {
        double percentInMonth = interestRate / (double) 12;
        double intermediateMonthlyPayment = Math.pow((1 + percentInMonth/100), (loanTerm - delay)) * (percentInMonth/100) /
                        ((Math.pow((1 + percentInMonth/100), (loanTerm - delay))) - 1) * amount;

        LinkedList<MonthlyPayment> monthlyPayments = new LinkedList<MonthlyPayment>();
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(issueDate);
        calendar.add(Calendar.MONTH, delay);

        for (int i = 0; i < loanTerm; i++) {
            MonthlyPayment loanPayment = new MonthlyPayment();

            calendar.add(Calendar.MONTH, 1);
            loanPayment.paymentDate = (calendar.getTime());

            loanPayment.number = (i + 1);
            if (i == 0) {
                loanPayment.amount = (intermediateMonthlyPayment);
                loanPayment.interestRateSum = (amount * percentInMonth/100);
                loanPayment.principalDebt = (loanPayment.amount - loanPayment.interestRateSum);
                loanPayment.contractDebt = (amount - loanPayment.principalDebt);
            }
            else {
                MonthlyPayment prevMonthlyPayment = monthlyPayments.get(i - 1);
                loanPayment.amount = prevMonthlyPayment.amount > 0.000001 ? intermediateMonthlyPayment : 0;
                loanPayment.interestRateSum = (prevMonthlyPayment.contractDebt * percentInMonth/100);
                loanPayment.principalDebt = (loanPayment.amount - loanPayment.interestRateSum);
                if (prevMonthlyPayment.contractDebt > 0.000001) {
                    loanPayment.contractDebt = (prevMonthlyPayment.contractDebt - loanPayment.principalDebt);
                }
                else {
                    loanPayment.contractDebt = (0);
                }
            }
            monthlyPayments.addLast(loanPayment);
        }

        //Correcting percents according to Roman's changes
        for (int i = 0; i < loanTerm; i++) {
            MonthlyPayment loanPayment = monthlyPayments.get(i);
            calendar.setTime(loanPayment.paymentDate);
            if (i == 0) {
                loanPayment.contractDebt = (amount);
            }
            else {
                MonthlyPayment prevMonthlyPayment = monthlyPayments.get(i - 1);
                loanPayment.contractDebt = (prevMonthlyPayment.contractDebt - prevMonthlyPayment.principalDebt);
            }
            loanPayment.creditPeriod = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            loanPayment.interestRateSum = (loanPayment.contractDebt * interestRate / 365 * loanPayment.creditPeriod / 100);
            loanPayment.amount = (loanPayment.interestRateSum + loanPayment.principalDebt);
        }
        return monthlyPayments;
    }

    public static Payment calculateInvestorPartAmount(double principalDebtPaymentAmount, double loanAmount, double personalInvestedFundsAmount, double personalInterestRate, int daysInMonth, double investmentPrincipalDebtRest) {
        return calculateInvestorPartAmount(principalDebtPaymentAmount, 0, -1, loanAmount, personalInvestedFundsAmount, personalInterestRate, daysInMonth, investmentPrincipalDebtRest);
    }
    public static Payment calculateInvestorPartAmount(double principalDebtPaymentAmount, double interestRatePaymentAmount, double totalInterestRateSumByPeriod, double loanAmount, double personalInvestedFundsAmount, double personalInterestRate, int daysInMonth, double investmentPrincipalDebtRest) {
        Payment payment = new Payment();
        payment.principalDebtAmount = calculateInvestorPart(loanAmount, personalInvestedFundsAmount) * principalDebtPaymentAmount;
        payment.interestRateSum = investmentPrincipalDebtRest * personalInterestRate / 100 / 365 * daysInMonth;
        if (totalInterestRateSumByPeriod != -1) {
            double part = payment.interestRateSum / totalInterestRateSumByPeriod;
            payment.interestRateSum = part * interestRatePaymentAmount;
        }
        return payment;
    }

    public static double calculateLoanInterest(Collection<Bet> bets) {
        double result = 0;
        for (Bet bet : bets) {
            result += bet.amountPart * bet.interestRate;
        }
        return result;
    }

    public static double calculateInvestorPart(double loanAmount, double personalInvestedFundsAmount) {
        return personalInvestedFundsAmount / loanAmount;
    }

    public static double calculateMonthlyPenalty(double currentDebt, double interestRate, double penaltyRatio, int dayCount) {
        return currentDebt * interestRate / 100 / 365 * penaltyRatio * dayCount;
    }

    public static double calculateDailyPenalty(double currentDebt, double interestRate, double penaltyRatio, int dayCount) {
        return currentDebt / 100 * interestRate * penaltyRatio * dayCount;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Long toDecimal(Double value) {
        if (value == null) {
            return null;
        }

        return (long) (round(value, 4) * 10000);
    }

    public static Double toFractional(Long value) {
        if (value == null) {
            return null;
        }
        return (double) value / 10000;
    }

    public static double toFractional2Rounded(long value) {
        return round(toFractional(value), 2);
    }

    public static Integer toDecimalFormatted(long value) {
        return ((Double) (FinanceUtils.toFractional2Rounded(value) * 100D)).intValue();
    }

    public static double round2(double value) {
        return round(value, 2);
    }

    public static String formatNumber(Long value) {
        Double dValue = round(toFractional(value), 2);


        return String.valueOf(dValue.longValue());
    }

    public static String formatNumber(Double value) {
        DecimalFormat formatter = new DecimalFormat();
        return formatter.format(round(value, 2));
    }

    public static class MonthlyPayment {
        public int number;
        public Date paymentDate;
        public double amount;
        public double interestRateSum;
        public double principalDebt;
        public double contractDebt;
        public int creditPeriod;
    }

    public static class Bet {
        public double interestRate;
        public double amountPart;
    }

    public static class Payment {
        public double principalDebtAmount;
        public double interestRateSum;
    }
}
